package com.wChartProgram.buss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wChartProgram.buss.mapper.UserInfoMapper;
import com.wChartProgram.buss.service.UserService;
import com.wChartProgram.common.componet.RedisComponet;
import com.wChartProgram.model.dto.LoginRequest;
import com.wChartProgram.model.dto.LoginResponse;
import com.wChartProgram.model.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserService {

    private static final String TOKEN_PREFIX = "token:";
    private static final long TOKEN_EXPIRE_TIME = 24; // token过期时间：24小时

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private RedisComponet redisComponet;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        // 1. 根据用户名查询用户
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getUsername, loginRequest.getUsername());
        UserInfo user = userInfoMapper.selectOne(queryWrapper);

        // 2. 验证用户是否存在
        if (user == null) {
            throw new RuntimeException("用户名不存在");
        }

        if (!user.getPassword().equals(loginRequest.getPassword())){
            throw new RuntimeException("用户密码错误!");
        }

        // 3. 验证用户状态
        if (user.getStatus() == null || user.getStatus() != 0) {
            throw new RuntimeException("用户已被禁用");
        }

        // 4. 验证密码（使用MD5加密）
        String encryptedPassword = DigestUtils.md5DigestAsHex(
            loginRequest.getPassword().getBytes(StandardCharsets.UTF_8)
        );
        if (!encryptedPassword.equals(user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 5. 生成token
        String token = UUID.randomUUID().toString().replace("-", "");
        String tokenKey = TOKEN_PREFIX + token;

        // 6. 将用户信息存储到Redis（设置过期时间）
        redisTemplate.opsForValue().set(tokenKey, String.valueOf(user.getId()), TOKEN_EXPIRE_TIME, TimeUnit.HOURS);

        // 7. 构造返回结果
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
            user.getId(),
            user.getUsername(),
            user.getRealName(),
            user.getPhone(),
            user.getEmail()
        );

        return new LoginResponse(token, userInfo);
    }

    @Override
    public void logout(String token) {
        String tokenKey = TOKEN_PREFIX + token;
        redisComponet.deleteRedisKey(tokenKey);
    }

    @Override
    public UserInfo getUserByToken(String token) {
        String tokenKey = TOKEN_PREFIX + token;
        String userId = redisComponet.getRedisValue(tokenKey).toString();

        if (userId == null) {
            return null;
        }

        return userInfoMapper.selectById(Long.parseLong(userId));
    }
}

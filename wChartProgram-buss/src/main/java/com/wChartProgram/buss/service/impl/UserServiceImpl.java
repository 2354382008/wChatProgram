package com.wChartProgram.buss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.wChartProgram.buss.mapper.UserInfoMapper;
import com.wChartProgram.buss.service.UserService;
import com.wChartProgram.common.componet.RedisComponet;
import com.wChartProgram.common.util.RsaLoginPasswordDecryptor;
import com.wChartProgram.model.dto.CommonResponseDto;
import com.wChartProgram.model.dto.LoginRequest;
import com.wChartProgram.model.dto.LoginResponse;
import com.wChartProgram.model.dto.UserInfoDto;
import com.wChartProgram.model.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
            log.info("前端校验为空之后提示请先注册账号！");
            return null;
        }
        try {
            //3.根据私钥解密
            String decryptedPassword = RsaLoginPasswordDecryptor.decryptRsaOaepSha256(loginRequest.getPasswordCipher());
            if (!user.getPassword().equals(decryptedPassword)){
                throw new RuntimeException("用户密码错误!");
            }
        }catch (Exception ex){
            throw new RuntimeException("密码解密失败");
        }
        // 4. 验证用户状态
        if (user.getStatus() == null || user.getStatus() != 0) {
            throw new RuntimeException("用户已被禁用");
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

    @Override
    public int register(UserInfoDto userInfoDto) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(userInfoDto.getUsername());
        userInfo.setPassword(userInfoDto.getConfirmPassword());
        userInfo.setPhone(userInfoDto.getPhone());
        userInfo.setEmail(userInfoDto.getEmail());
        userInfo.setRealName(userInfoDto.getUsername());
        userInfo.setStatus(1);
        return userInfoMapper.insert(userInfo);
    }

    @Override
    public Boolean checkInterface(UserInfoDto userInfoDto) {
        //查询是否已存在该账户
        UserInfo userInfo = userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>()
                .eq(UserInfo::getUsername, userInfoDto.getUsername()));
        if (ObjectUtils.isEmpty(userInfo)){
            return false;
        }
        return true;
    }

    @Override
    public String generateInviteCode() {
        // 1. 生成唯一的邀请码
        String inviteCode = UUID.randomUUID().toString().replace("-", "");
        String weChatUrl = "https://weixin.qq.com/invite?code=" + inviteCode;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix;
        try {
            bitMatrix = qrCodeWriter.encode(weChatUrl, BarcodeFormat.QR_CODE, 300, 300);
        } catch (WriterException e) {
            throw new RuntimeException("Failed to generate QR code", e);
        }
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            String qrCodeBase64 = Base64.encodeBase64String(outputStream.toByteArray());
            return qrCodeBase64;
        }catch (IOException e){
            throw new RuntimeException("生成二维码base64失败!", e);
        }
    }
}

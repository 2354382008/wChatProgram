package com.wChartProgram.buss.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wChartProgram.model.dto.CommonResponseDto;
import com.wChartProgram.model.dto.LoginRequest;
import com.wChartProgram.model.dto.LoginResponse;
import com.wChartProgram.model.dto.UserInfoDto;
import com.wChartProgram.model.entity.UserInfo;

/**
 * 用户服务接口
 */
public interface UserService extends IService<UserInfo> {

    /**
     * 用户登录
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest loginRequest);

    /**
     * 用户登出
     * @param token 登录token
     */
    void logout(String token);

    /**
     * 根据token获取用户信息
     * @param token 登录token
     * @return 用户信息
     */
    UserInfo getUserByToken(String token);

    int register(UserInfoDto userInfoDto);

    Boolean checkInterface(UserInfoDto userInfoDto);

    String generateInviteCode();
}

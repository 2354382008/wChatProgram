package com.wChartProgram.buss.controller;

import com.wChartProgram.buss.service.UserService;
import com.wChartProgram.model.dto.CommonRequestDto;
import com.wChartProgram.model.dto.CommonResponseDto;
import com.wChartProgram.model.dto.LoginRequest;
import com.wChartProgram.model.dto.LoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 登录控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/wChat/auth")
public class LoginController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录接口
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    @PostMapping("/login")
    public CommonResponseDto<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        log.info("用户登录请求：username={}", loginRequest.getUsername());
        try {
            return CommonResponseDto.create().data(userService.login(loginRequest));
        } catch (Exception e) {
            log.error("用户登录失败：username={}, error={}", loginRequest.getUsername(), e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 用户登出接口
     * @param token 登录token
     * @return 操作结果
     */
    @PostMapping("/logout")
    public String logout(@RequestHeader(value = "Authorization", defaultValue = "") String token) {
        log.info("用户登出请求：token={}", token);
        try {
            userService.logout(token);
            log.info("用户登出成功");
            return "登出成功";
        } catch (Exception e) {
            log.error("用户登出失败：error={}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}

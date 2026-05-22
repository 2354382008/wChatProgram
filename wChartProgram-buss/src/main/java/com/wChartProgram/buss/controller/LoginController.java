package com.wChartProgram.buss.controller;

import com.wChartProgram.buss.service.UserService;
import com.wChartProgram.common.util.PrivateKeyUtil;
import com.wChartProgram.model.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.commons.codec.binary.Base64;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.ByteArrayOutputStream;
import java.nio.file.FileSystems;
import java.util.UUID;

/**
 * 登录控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/wChat/auth")
public class LoginController {

    private static final String BEGIN_PUBLIC_KEY = "BEGIN PUBLIC KEY";

    @Value("${wchat.login.rsa-public-key-location:classpath:rsa_public.pem}")
    private Resource publicKeyFile;

    @Value("${wchat.login.rsa-private-key-location:classpath:rsa_private.pem}")
    private Resource privateKeyFile;

    @Autowired
    private UserService userService;

    /**
     * 获取公钥
     */
    @GetMapping("/publicKey")
    public CommonResponseDto<LoginPublicKeyData> publicKey() throws IOException {
        if (publicKeyFile == null && !publicKeyFile.exists()) {
            return CommonResponseDto.error("500001", "登录公钥未配置");
        }
        String pem = StreamUtils.copyToString(publicKeyFile.getInputStream(), StandardCharsets.UTF_8)
                .replace("\\r\\n", "\n")
                .replace("\r\n", "\n")
                .trim();
        if (!StringUtils.hasText(pem) || !pem.contains(BEGIN_PUBLIC_KEY)) {
            return CommonResponseDto.error("500001", "登录公钥未配置");
        }
        LoginPublicKeyData data = new LoginPublicKeyData();
        data.setPublicKeyPem(pem);
        return CommonResponseDto.sucess(data);
    }

    /**
     * 用户登录接口
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    @PostMapping("/login")
    public CommonResponseDto<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        log.info("用户登录请求：username={}", loginRequest.getUsername());
        try {
            return CommonResponseDto.sucess(userService.login(loginRequest));
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

    /**
     * 获取私钥
     * @return 操作结果
     */
    @PostMapping("/testPrivateKey")
    public String getPrivateKey() throws Exception{
        return PrivateKeyUtil.getPrivateKey();
    }

    /**
     * 根据私钥获取公钥
     * @return 操作结果
     */
    @PostMapping("/testPublicKey")
    public CommonResponseDto<String> getPublicKey() throws Exception{
        if (privateKeyFile == null && !privateKeyFile.exists()) {
            return CommonResponseDto.error("500001", "登录公钥未配置");
        }
        String pem = StreamUtils.copyToString(privateKeyFile.getInputStream(), StandardCharsets.UTF_8)
                .replace("\\r\\n", "\n")
                .replace("\r\n", "\n")
                .trim();
        return CommonResponseDto.sucess(PrivateKeyUtil.getPublicKeyPemFromPrivateKeyPem(pem));
    }

    /**
     * 注册用户
     * @return 返回结果
     */
    @PostMapping("/register")
    public CommonResponseDto<Integer> register(@RequestBody UserInfoDto userInfoDto) throws Exception{
        return CommonResponseDto.sucess(userService.register(userInfoDto));
    }



    /**
     * 校验账号是否已存在
     * @return 操作结果
     */
    @PostMapping("{module}/checkInterface")
    public CommonResponseDto<Boolean> checkInterface(@RequestBody UserInfoDto userInfoDto) throws Exception{
        return CommonResponseDto.sucess(userService.checkInterface(userInfoDto));
    }

    /**
     * 生成邀请码
     * @return 返回结果
     */
    @PostMapping("/generateInviteCode")
    public CommonResponseDto<String> generateInviteCode() throws Exception {
        return CommonResponseDto.sucess(userService.generateInviteCode());
    }
}

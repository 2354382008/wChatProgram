//package com.wChartProgram.buss.config;
//
//import com.wChartprogram.security.componet.SecurityAccessDeniedHandler;
//import com.wChartprogram.security.componet.SecurityAuthenticationEntryPoint;
//import com.wChartprogram.security.config.IgnoreUrlsConfig;
//import com.wChartprogram.security.config.MySecurityConfig;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//
///**
// * 可扩展认证与权处理
// */
//public class ProgramBussSecurityConfig extends MySecurityConfig {
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        super.configure(http);
//    }
//
//    @Override
//    protected IgnoreUrlsConfig ignoreUrlsConfig() {
//        return super.ignoreUrlsConfig();
//    }
//
//    @Override
//    public SecurityAccessDeniedHandler securityaccessDeniedHandler() {
//        return super.securityaccessDeniedHandler();
//    }
//
//    @Override
//    public SecurityAuthenticationEntryPoint securityAuthenticationEntryPoint() {
//        return super.securityAuthenticationEntryPoint();
//    }
//}

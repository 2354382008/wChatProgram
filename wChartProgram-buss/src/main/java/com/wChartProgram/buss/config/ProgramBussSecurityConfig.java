package com.wChartProgram.buss.config;

import com.wChartProgram.security.config.SecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

//标记为一个配置类
@Configuration
//去启动security
@EnableWebSecurity
public class ProgramBussSecurityConfig extends SecurityConfig {
}

package com.wChartProgram.security.config;

import com.wChartProgram.security.componet.SecurityAccessDeniedHandler;
import com.wChartProgram.security.componet.SecurityAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * 配置安全策略（主要做权限，是一个认证和授权的框架）
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 权限配置，白名单，jwt认证
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http.authorizeRequests();
        //1.循环白名单进行放行
        ignoreUrlsConfig().getUrls().forEach(url ->{
            registry.antMatchers(url).permitAll();
        });
        //2.允许请求跨域,浏览器自己多发送的OPTIONS请求放行,OPTIONS请求只会携带自定义的字段，并不会将相应的值带入进去
        registry.antMatchers(HttpMethod.OPTIONS).permitAll();
        //3.其他任何请求都需要身份认证
        registry.anyRequest()
                .authenticated()
                //关闭csrf跨站请求伪造
                .and()
                .csrf()
                //禁用
                .disable()
                //禁止session，session会增加服务器压力
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                //自定义权限拒绝处理类
                .and()
                .exceptionHandling()
                //没有权限访问时的处理类
                .accessDeniedHandler(securityaccessDeniedHandler())
                //没有登录时的处理类
                .authenticationEntryPoint(securityAuthenticationEntryPoint());
    }

    /**
     * 白名单配置
     * @return
     */
    @Bean
    protected IgnoreUrlsConfig ignoreUrlsConfig(){
        return new IgnoreUrlsConfig();
    }

    /**
     * 没有权限时的处理类
     * @return
     */
    @Bean
    public SecurityAccessDeniedHandler securityaccessDeniedHandler(){
        return new SecurityAccessDeniedHandler();
    }

    /**
     * 没有登录时的处理类
     * @return
     */
    @Bean
    public SecurityAuthenticationEntryPoint securityAuthenticationEntryPoint(){
        return new SecurityAuthenticationEntryPoint();
    }

}

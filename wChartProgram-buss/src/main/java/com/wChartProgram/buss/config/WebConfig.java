package com.wChartProgram.buss.config;

import com.wChartProgram.buss.interceptor.CustomerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private CustomerInterceptor customerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(customerInterceptor)
                .addPathPatterns("/**")//指定拦截的路径
                .excludePathPatterns("/program");//排除的路径
    }
}

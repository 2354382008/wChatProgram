package com.wChartProgram.start;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.mvc.Controller;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 生命周期：
 * 实例化(spring容器通过反射或构造器创建bean实例，单例bean在容器启动时创建，原型bean在每次请求时创建)
 * 属性赋值（通过依赖注入（构造器注入，setter注入或字段）设置属性）
 * 初始化（@postconstruct注解方法或InitalizingBean.afterPropertiessec()）
 * 使用（bean完全初始化后，可被应用程序调用）
 * 销毁（容器关闭时调用）
 */
@Slf4j
@SpringBootApplication(scanBasePackages = {"com.wChartProgram.**"})
@MapperScans(@MapperScan({"com.wChartProgram.**.mapper*"}))
@EnableCaching
@EnableConfigurationProperties
public class RunApplication {
    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(RunApplication.class,args);
        Environment env = applicationContext.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port");
        log.info("\n----------------------------------------------------------\n\t" +
                "Application is running! Access URLs:\n\t" +
                "Local: \t\thttp://localhost:" + port  + "/\n\t" +
                "External: \thttp://" + ip + ":" + port  + "/\n\t" +
                "----------------------------------------------------------");
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                log.info("执行 JVM ShutDownHook...");
            }
        }));
    }
}

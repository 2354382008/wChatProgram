package com.wChartProgram.buss.constroct;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
public class ConstroctConfig {

    public ConstroctConfig() {
        log.info("====== 加载ConstroctConfig配置类 ======");
    }

    @Bean
    @ConditionalOnMissingBean
    public ConstroctManager constroctOpr(){
        return new ConstroctManager() {
            @PostConstruct
            protected void init() {
                super.init();
            }

            @Override
            protected List<String> getStrsList() {
                return Arrays.asList("1","2","3","4");
            }
        };
    }
}

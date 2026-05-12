package com.wChartProgram.buss.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaMessageListener {

//    @KafkaListener(topics = "demo",groupId = "test-consumer-group")
    public void kafkaListener(String message){
        log.info("监听到kafka消息为：{}",message);
    }
}

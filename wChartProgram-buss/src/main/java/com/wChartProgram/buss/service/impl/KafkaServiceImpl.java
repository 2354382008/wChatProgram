package com.wChartProgram.buss.service.impl;

import com.wChartProgram.buss.service.KafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaServiceImpl implements KafkaService {

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    @Override
    public void sendMessage(String topic, String message) {
        topic = "test_kafka_sendMessage";
        message.concat("测试kafka发送消息");
        kafkaTemplate.send(topic, message).addCallback(
                success -> System.out.println("Message sent successfully: " + message),
                failure -> System.err.println("Failed to send message: " + failure.getMessage())
        );
    }

}

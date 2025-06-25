package com.wChartProgram.buss.service;

public interface KafkaService {

    void sendMessage(String topic, String message);
}

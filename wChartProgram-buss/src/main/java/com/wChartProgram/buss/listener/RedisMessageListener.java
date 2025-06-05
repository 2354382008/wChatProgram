package com.wChartProgram.buss.listener;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * 消息监听
 * 记订阅者，可订阅多个频道 不支持持久化或确认机制
 * 若发送的消息没有订阅者，则这条消息将丢失，不会存储内存
 * redis 实时性高，数据是存储与内存的
 * redis是广播形势，一条消息可以有多个订阅者
 * redis没有消费者的负载均衡
 */
@Component
public class RedisMessageListener implements MessageListener {
    @Override
    public void onMessage(Message message, byte[] bytes) {
        String body = new String(message.getBody()); // 获取消息体内容
        System.out.println("监听到的消息为 : " + body); // 处理消息逻辑
    }
}

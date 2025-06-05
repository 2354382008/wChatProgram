package com.wChartProgram.common.componet;

public interface RedisComponet {

    /**
     * 发送消息
     * @param key
     * @param object
     */
    void sendMessage(String key,Object object);

    /**
     * 获取redis 存储的key的值
     * @param key
     * @return
     */
    Object getRedisValue(String  key);

    /**
     * 向redis存储值
     * @param key
     * @param object
     */
    void addRedisKey(String key,Object object);
}

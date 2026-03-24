package com.wChartProgram.common.componet.impl;

import com.wChartProgram.common.componet.RedisComponet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisComponetImpl implements RedisComponet {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public void sendMessage(String key, Object object) {
        redisTemplate.convertAndSend(key, object);
    }

    @Override
    public Object getRedisValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void addRedisKey(String key, Object object) {
        redisTemplate.opsForValue().set(key, object);
    }

    @Override
    public void deleteRedisKey(String key) {
        redisTemplate.delete(key);
    }
}

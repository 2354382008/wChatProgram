package com.wChartProgram.buss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wChartProgram.buss.mapper.LeaderRoleMapper;
import com.wChartProgram.buss.service.LeaderRoleService;
import com.wChartProgram.common.componet.RedisComponet;
import com.wChartProgram.common.enums.HandlerCode;
import com.wChartProgram.common.enums.StatusCode;
import com.wChartProgram.model.entity.LeaderRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
*
*/
@Slf4j
@Service
public class LeaderRoleServiceImpl extends ServiceImpl<LeaderRoleMapper, LeaderRole>
implements LeaderRoleService {

    @Autowired
    private LeaderRoleMapper leaderRoleMapper;

    /**
     * 是spring定义的注解
     * 是先根据类型（byType）查找，如果存在多个 Bean 再根据名称（byName）进行查找
     * 只有一个required参数可设置
     * 支持属性，构造方法，setter注入
     */
    @Autowired
    private RedisComponet redisComponet;

    /**
     * java定义的注解
     * 是先根据名称查找，如果（根据名称）查找不到，再根据类型进行查找
     * 有name和type参数可设置
     * 支持属性注入
     */
    @Resource
    private StateMachine<StatusCode,HandlerCode> stateMachine;

    @Override
    public List<LeaderRole> queryLeaderList() {
        redisComponet.addRedisKey("redisKey1","测试redis存储值");
        System.out.println("测试redis获取值:"+redisComponet.getRedisValue("redisKey1"));
        redisComponet.sendMessage("redisKey2","redis发送消息");
        LambdaQueryWrapper<LeaderRole> queryWrapper = new LambdaQueryWrapper<>();
        return leaderRoleMapper.selectList(queryWrapper);
    }

    @Override
    public synchronized void notice() {
        log.info("走到message");
        Message message = MessageBuilder.withPayload(HandlerCode.NOTICE).
                setHeader("order", "1").build();
        sendEvent(message);

    }

    /**
     * 使用多线程更新状态
     * 按每1000条分一批次执行
     */
    @Override
    public void updateSts() {

    }

    public void sendEvent(Message<HandlerCode> message){
        log.info("走到start");
        stateMachine.start();
        log.info("走到start");
        stateMachine.sendEvent(message);
        StatusCode statusCode = stateMachine.getState().getId();
        log.info("当前状态{}",statusCode);
    }
}

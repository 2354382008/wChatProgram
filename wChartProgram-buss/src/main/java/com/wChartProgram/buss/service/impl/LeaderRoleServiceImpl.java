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

    /**
     * 状态机是否已启动的标志
     */
    private volatile boolean stateMachineStarted = false;

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
        log.info("执行通知操作");
        Message message = MessageBuilder.withPayload(HandlerCode.NOTICE)
                .setHeader("order", "1")
                .setHeader("timestamp", System.currentTimeMillis())
                .build();
        boolean result = sendEvent(message);
        log.info("通知操作执行结果: {}", result ? "成功" : "失败");
    }

    /**
     * 使用多线程更新状态
     * 按每1000条分一批次执行
     */
    @Override
    public void updateSts() {

    }

    /**
     * 发送事件到状态机
     * @param message 事件消息
     * @return 事件是否被成功接受
     */
    public boolean sendEvent(Message<HandlerCode> message){
        try {
            // 确保状态机已启动
            ensureStateMachineStarted();

            log.info("发送事件: {}, 当前状态: {}",
                message.getPayload(), getCurrentState());

            // 发送事件并获取结果
            boolean accepted = stateMachine.sendEvent(message);

            if (accepted) {
                StatusCode currentState = getCurrentState();
                log.info("事件被接受，当前状态: {}", currentState);
            } else {
                log.warn("事件未被接受: {}", message.getPayload());
            }

            return accepted;

        } catch (Exception e) {
            log.error("发送事件时发生异常: {}", message.getPayload(), e);
            return false;
        }
    }

    /**
     * 确保状态机已启动
     */
    private void ensureStateMachineStarted() {
        if (!stateMachineStarted) {
            synchronized (this) {
                if (!stateMachineStarted) {
                    log.info("启动状态机");
                    stateMachine.start();
                    stateMachineStarted = true;
                    log.info("状态机启动成功，初始状态: {}", getCurrentState());
                }
            }
        }
    }

    /**
     * 获取当前状态
     * @return 当前状态枚举
     */
    public StatusCode getCurrentState() {
        try {
            return stateMachine.getState().getId();
        } catch (Exception e) {
            log.error("获取当前状态时发生异常", e);
            // 返回默认状态
            return StatusCode.IS_NOTICE_N;
        }
    }

    /**
     * 重置状态机到初始状态
     */
    public synchronized void resetStateMachine() {
        try {
            log.info("重置状态机到初始状态");
            stateMachine.stop();
            stateMachineStarted = false;
            ensureStateMachineStarted();
            log.info("状态机重置完成，当前状态: {}", getCurrentState());
        } catch (Exception e) {
            log.error("重置状态机时发生异常", e);
        }
    }

    /**
     * 执行操作事件
     * @return 事件是否被成功接受
     */
    public boolean execute() {
        log.info("执行操作");
        Message message = MessageBuilder.withPayload(HandlerCode.EXECUT)
                .setHeader("order", "2")
                .setHeader("timestamp", System.currentTimeMillis())
                .build();
        return sendEvent(message);
    }

    /**
     * 添加操作事件
     * @return 事件是否被成功接受
     */
    public boolean addHandler() {
        log.info("执行添加操作");
        Message message = MessageBuilder.withPayload(HandlerCode.ADD_HANDLER)
                .setHeader("operator", "add")
                .setHeader("timestamp", System.currentTimeMillis())
                .build();
        return sendEvent(message);
    }

    /**
     * 更新操作事件
     * @return 事件是否被成功接受
     */
    public boolean updateHandler() {
        log.info("执行更新操作");
        Message message = MessageBuilder.withPayload(HandlerCode.UDATE_HANDLER)
                .setHeader("operator", "update")
                .setHeader("timestamp", System.currentTimeMillis())
                .build();
        return sendEvent(message);
    }

    /**
     * 获取状态机状态信息
     * @return 状态信息字符串
     */
    public String getStateMachineInfo() {
        StringBuilder info = new StringBuilder();
        info.append("状态机信息:\n");
        info.append("  已启动: ").append(stateMachineStarted).append("\n");
        info.append("  当前状态: ").append(getCurrentState()).append("\n");
        info.append("  当前状态描述: ").append(getCurrentState().getMsg()).append("\n");
        return info.toString();
    }
}

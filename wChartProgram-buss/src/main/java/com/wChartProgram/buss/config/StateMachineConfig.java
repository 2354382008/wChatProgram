package com.wChartProgram.buss.config;

import com.wChartProgram.common.enums.HandlerCode;
import com.wChartProgram.common.enums.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

/**
 * 状态机配置类
 * 配置状态机的状态、事件和状态转换规则
 */
@Slf4j
@Configuration
@EnableStateMachine
class StateMachineConfig extends StateMachineConfigurerAdapter<StatusCode, HandlerCode> {

    /**
     * 配置状态机的状态
     */
    @Override
    public void configure(StateMachineStateConfigurer<StatusCode, HandlerCode> states) throws Exception {
        states
            .withStates()
            .initial(StatusCode.IS_NOTICE_N)
            .states(EnumSet.allOf(StatusCode.class))
            // 定义状态结束点
            .end(StatusCode.EXECUTE_STS_Y)
            .end(StatusCode.EXECUTE_STS_O);
    }

    /**
     * 配置状态机的状态转换规则
     */
    @Override
    public void configure(StateMachineTransitionConfigurer<StatusCode, HandlerCode> transitions) throws Exception {
        transitions
            // 通知事件：从未通知到已通知
            .withExternal()
                .source(StatusCode.IS_NOTICE_N)
                .target(StatusCode.IS_NOTICE_Y)
                .event(HandlerCode.NOTICE)
                .and()
            // 执行事件：从已通知到已执行
            .withExternal()
                .source(StatusCode.IS_NOTICE_Y)
                .target(StatusCode.EXECUTE_STS_Y)
                .event(HandlerCode.EXECUT)
                .and()
            // 添加操作：从未执行到已执行
            .withExternal()
                .source(StatusCode.EXECUTE_STS_N)
                .target(StatusCode.EXECUTE_STS_Y)
                .event(HandlerCode.ADD_HANDLER)
                .and()
            // 更新操作：保持当前状态
            .withExternal()
                .source(StatusCode.EXECUTE_STS_Y)
                .target(StatusCode.EXECUTE_STS_Y)
                .event(HandlerCode.UDATE_HANDLER);
    }

    /**
     * 状态机监听器Bean
     */
    @Bean
    public StateMachineListenerAdapter<StatusCode, HandlerCode> stateMachineListener() {
        return new StateMachineListenerAdapter<StatusCode, HandlerCode>() {
            @Override
            public void stateChanged(State<StatusCode, HandlerCode> from, State<StatusCode, HandlerCode> to) {
                log.info("状态转换: {} -> {}",
                    from != null ? from.getId() : "无",
                    to != null ? to.getId() : "无");
            }

            @Override
            public void eventNotAccepted(org.springframework.messaging.Message<HandlerCode> event) {
                log.warn("事件未被接受: {}", event.getPayload());
            }

            @Override
            public void transition(org.springframework.statemachine.transition.Transition<StatusCode, HandlerCode> transition) {
                log.info("状态转换详情: 触发事件 {}, 源状态 {}, 目标状态 {}",
                    transition.getTrigger().getEvent(),
                    transition.getSource().getId(),
                    transition.getTarget().getId());
            }

            @Override
            public void stateMachineError(StateMachine<StatusCode, HandlerCode> stateMachine, Exception exception) {
                log.error("状态机发生错误", exception);
            }
        };
    }
}

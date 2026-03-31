package com.wChartProgram.common.config;

import com.wChartProgram.common.enums.HandlerCode;
import com.wChartProgram.common.enums.StatusCode;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import java.util.EnumSet;

/**
 * 状态机配置类
 * @author wangmq
 */
@Configuration
@EnableStateMachine
public class StatusMachtueConfig extends EnumStateMachineConfigurerAdapter<StatusCode, HandlerCode> {

    /**
     * 状态配置
     * @param states
     * @throws Exception
     */
    @Override
    public void configure(StateMachineStateConfigurer<StatusCode, HandlerCode> states) throws Exception {
        states.withStates()
                // 设置初始状态为"未执行"
                .initial(StatusCode.EXECUTE_STS_N)
                // 注册所有任务状态枚举
                .states(EnumSet.allOf(StatusCode.class));
    }

    /**
     * 事件配置
     * @param transitionConfigurer
     * @throws Exception
     */
    @Override
    public void configure(StateMachineTransitionConfigurer<StatusCode, HandlerCode> transitionConfigurer) throws Exception {
        // 定义支付事件触发的状态转换
        transitionConfigurer.withExternal()
                // 源状态：未通知
                .source(StatusCode.IS_NOTICE_N)
                // 目标状态：已通知
                .target(StatusCode.IS_NOTICE_Y)
                // 触发事件：通知
                .event(HandlerCode.NOTICE)
                .and()
                .withExternal()
                // 源状态：未执行
                .source(StatusCode.EXECUTE_STS_N)
                // 目标状态：已知晓未执行
                .target(StatusCode.EXECUTE_STS_O)
                //触发事件执行
                .event(HandlerCode.EXECUT)
                .and()
                .withExternal()
                //源状态：已知晓未执行
                .source(StatusCode.EXECUTE_STS_O)
                //目标状态：已执行
                .target(StatusCode.EXECUTE_STS_Y)
                //触发事件：执行
                .event(HandlerCode.EXECUT);
    }
}

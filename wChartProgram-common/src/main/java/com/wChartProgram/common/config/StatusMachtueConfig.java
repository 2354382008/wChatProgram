package com.wChartProgram.common.config;

import com.wChartProgram.common.enums.HandlerCode;
import com.wChartProgram.common.enums.StatusCode;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import java.util.EnumSet;

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
                .initial(StatusCode.EXECUTE_STS_N) // 设置初始状态为"未执行"
                .states(EnumSet.allOf(StatusCode.class)); // 注册所有任务状态枚举
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
                .source(StatusCode.IS_NOTICE_N)  // 源状态：未通知
                .target(StatusCode.IS_NOTICE_Y)  // 目标状态：已通知
                .event(HandlerCode.NOTICE)  // 触发事件：通知
                .and()
                .withExternal()
                .source(StatusCode.EXECUTE_STS_N)  // 源状态：未执行
                .target(StatusCode.EXECUTE_STS_O)  // 目标状态：已知晓未执行
                .event(HandlerCode.EXECUT) //触发事件执行
                .and()
                .withExternal()
                .source(StatusCode.EXECUTE_STS_O) //源状态：已知晓未执行
                .target(StatusCode.EXECUTE_STS_Y) //目标状态：已执行
                .event(HandlerCode.EXECUT);//触发事件：执行
    }
}

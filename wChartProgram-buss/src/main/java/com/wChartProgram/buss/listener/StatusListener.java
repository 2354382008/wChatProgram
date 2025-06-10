package com.wChartProgram.buss.listener;

import com.wChartProgram.common.enums.HandlerCode;
import com.wChartProgram.common.enums.StatusCode;
import org.springframework.statemachine.annotation.OnTransitionEnd;
import org.springframework.statemachine.annotation.OnTransitionStart;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

@Component
@WithStateMachine
public class StatusListener{

    @OnTransitionStart
    public void onTransitionStart(Transition<StatusCode,HandlerCode> transition){
        System.out.printf("[状态机日志] 开始转换：%s -> %s (事件：%s)%n",
                transition.getSource().getId(),
                transition.getTarget().getId(),
                transition.getTrigger().getEvent());
    }

    /**
     * 状态转换完成时触发
     */
    @OnTransitionEnd
    public void onTransitionEnd(Transition<StatusCode, HandlerCode> transition) {
        System.out.println("[状态机日志] 转换完成，当前状态："
                + transition.getSource().getId());
    }
}

package com.wChartProgram.buss.constroct.executor;

import com.wChartProgram.buss.constroct.ConstroctManager;
import com.wChartProgram.model.dto.CommonRequestDto;
import com.wChartProgram.model.dto.CommonResponseDto;
import lombok.extern.slf4j.Slf4j;

/**
 * 公共基础执行器
 * @param <T>
 * @param <R>
 */
@Slf4j
public abstract class BaseExecutor<T extends CommonRequestDto,R extends CommonResponseDto> {

    public R executor(T request){
        //基础校验-必校验登录信息:LoginExecutor
        ConstroctManager.executeVoidAsync(LoginExecutor.class,function -> function.executor(request));
        volitedHandle(request);
        return executorHnadle(request);
    }

    /**
     * 实际实行逻辑
     * @param request
     * @return
     */
    protected abstract R executorHnadle(T request);

    public void volitedHandle(T request){
        //默认不校验，子类自己写自己的校验逻辑
        log.info("BaseExecutor.volitedHandle:默认不校验，子类自己写自己的校验逻辑");
    }
}

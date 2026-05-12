package com.wChartProgram.buss.constroct.executor.impl;

import com.wChartProgram.buss.constroct.executor.LoginExecutor;
import com.wChartProgram.model.dto.CommonRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 登录权限校验
 * @author wmq
 */
@Slf4j
@Component
public class LoginPermionExecutorImpl extends LoginExecutor<CommonRequestDto> {

    @Override
    protected void executorPt(CommonRequestDto request) {
        //todo 登录权限校验
        log.info("登录权限校验待办");
    }
}

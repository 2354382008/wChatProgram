package com.wChartProgram.buss.factory.handler.impl;

import com.wChartProgram.buss.factory.WorkingAbstract;
import com.wChartProgram.common.enums.HandlerCode;
import org.springframework.stereotype.Service;

@Service
public class UpdateWorkingHandlerImpl extends WorkingAbstract {
    @Override
    public HandlerCode getHandler() {
        return HandlerCode.UDATE_HANDLER;
    }

    @Override
    public void test() {

    }
}

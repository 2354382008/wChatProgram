package com.wChartProgram.buss.factory.handler.impl;

import com.wChartProgram.buss.factory.WorkingAbstract;
import com.wChartProgram.common.enumCode.HandlerCode;
import org.springframework.stereotype.Service;

@Service
public class AddWorkingHandlerImpl extends WorkingAbstract {

    @Override
    public HandlerCode getHandler() {
        return HandlerCode.ADD_HANDLER;
    }

    @Override
    public void test() {
        testAbstract();
    }
}

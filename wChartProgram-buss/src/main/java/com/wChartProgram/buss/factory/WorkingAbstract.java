package com.wChartProgram.buss.factory;

import com.wChartProgram.buss.factory.handler.WorkingHandler;
import com.wChartProgram.common.enumCode.HandlerCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public abstract class WorkingAbstract implements WorkingHandler {

    public void testAbstract(){
        log.info("测试添加");
    }

}

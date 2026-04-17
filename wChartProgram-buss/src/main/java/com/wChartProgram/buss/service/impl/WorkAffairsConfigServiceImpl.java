package com.wChartProgram.buss.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wChartProgram.buss.mapper.WorkAffairsConfigMapper;
import com.wChartProgram.buss.service.WorkAffairsConfigService;
import com.wChartProgram.common.enums.HandlerCode;
import com.wChartProgram.common.enums.StatusCode;
import com.wChartProgram.model.entity.WorkAffairsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

/**
*
*/
@Service
public class WorkAffairsConfigServiceImpl extends ServiceImpl<WorkAffairsConfigMapper, WorkAffairsConfig>
implements WorkAffairsConfigService {
    @Autowired
    private StateMachine<StatusCode, HandlerCode> stateMachine;

    public void execute(){
        stateMachine.sendEvent(HandlerCode.NOTICE);
    }
}

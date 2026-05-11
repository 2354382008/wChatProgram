package com.wChartProgram.buss.constroct;

import com.wChartProgram.model.dto.ConstroctOneDto;
import com.wChartProgram.model.dto.ConstroctTwoDto;
import org.springframework.stereotype.Service;

@Service
public class TwoImpl extends ConstroctInterfaceTwoImpl{
    @Override
    protected void doSomething(ConstroctTwoDto constroctTwoDto) {
        System.out.println("进入：TwoImpl");
    }
}

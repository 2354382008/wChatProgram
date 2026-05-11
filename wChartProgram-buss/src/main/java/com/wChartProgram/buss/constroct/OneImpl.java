package com.wChartProgram.buss.constroct;

import com.wChartProgram.model.dto.ConstroctOneDto;
import org.springframework.stereotype.Service;

@Service
public class OneImpl extends ConstroctInterfaceOneImpl{
    @Override
    protected void doSomething(ConstroctOneDto constroctOneDto) {
        System.out.println("进入：OneImpl");
    }
}

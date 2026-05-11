package com.wChartProgram.buss.constroct;

import com.wChartProgram.model.dto.ConstroctThreeDto;
import org.springframework.stereotype.Service;

@Service
public class ThreeImpl extends ConstroctInterfaceThreeImpl{

    @Override
    protected void doSomething(ConstroctThreeDto constroctThreeDto) {
        System.out.println("进入：ThreeImpl");
    }
}

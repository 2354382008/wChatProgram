package com.wChartProgram.buss.constroct;

import com.wChartProgram.model.dto.ConstroctTwoDto;

public abstract class ConstroctInterfaceTwoImpl implements ConstroctInterface {
    @Override
    public Class<ConstroctInterfaceTwoImpl> getInterfaceClass() {
        return ConstroctInterfaceTwoImpl.class;
    }

    protected abstract void doSomething(ConstroctTwoDto constroctTwoDto);

}
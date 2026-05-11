package com.wChartProgram.buss.constroct;


import com.wChartProgram.model.dto.ConstroctOneDto;

public abstract class ConstroctInterfaceOneImpl implements ConstroctInterface {
    @Override
    public Class<ConstroctInterfaceOneImpl> getInterfaceClass() {
        return ConstroctInterfaceOneImpl.class;
    }

    protected abstract void doSomething(ConstroctOneDto constroctOneDto);
}
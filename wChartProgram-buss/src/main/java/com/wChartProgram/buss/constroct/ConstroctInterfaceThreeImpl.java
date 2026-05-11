package com.wChartProgram.buss.constroct;

import com.wChartProgram.model.dto.ConstroctThreeDto;
import org.springframework.stereotype.Service;

@Service
public abstract class ConstroctInterfaceThreeImpl implements ConstroctInterface {
    @Override
    public Class<ConstroctInterfaceThreeImpl> getInterfaceClass() {
        return ConstroctInterfaceThreeImpl.class;
    }

    protected abstract void doSomething(ConstroctThreeDto constroctThreeDto);
}
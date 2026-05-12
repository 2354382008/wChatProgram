package com.wChartProgram.buss.constroct.executor;

import com.wChartProgram.buss.constroct.ConstroctInterface;
import com.wChartProgram.model.dto.CommonRequestDto;

/**
 * 数据扩展校验器
 */
public abstract class DataExecutor<T extends CommonRequestDto> implements ConstroctInterface {

    @Override
    public Class<? extends ConstroctInterface> getInterfaceClass() {
        return DataExecutor.class;
    }

    public void executor(T request){
        executorPt(request);
    }

    protected abstract void executorPt(T request);
}

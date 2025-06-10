package com.wChartProgram.buss.factory;

import com.wChartProgram.buss.factory.handler.WorkingHandler;
import com.wChartProgram.common.enums.HandlerCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 创建工作处理工厂
 */
@Component
public class WorkingFactory {

    private static final Map<HandlerCode, WorkingHandler> handlerMap = new HashMap<>();

    @Autowired
    private List<WorkingHandler> workingHandlerList;

    @PostConstruct
    public void init(){
        workingHandlerList.forEach(workingHandler -> {
            handlerMap.put(workingHandler.getHandler(),workingHandler);
        });
    }

    public WorkingHandler getHandler(HandlerCode handlerCode){
        return handlerMap.get(handlerCode);
    }
}

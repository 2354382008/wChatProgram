package com.wChartProgram.buss.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture; /**
 * 流程配置类
 */
public class DynamicFlowConfig {
    private List<DynamicAsyncStep> steps = new ArrayList<>();
    
    public DynamicFlowConfig addStep(DynamicAsyncStep step) {
        steps.add(step);
        return this;
    }
    
    public DynamicFlowConfig addSyncStep(DynamicSyncStep step) {
        steps.add(context -> {
            step.execute(context);
            return CompletableFuture.completedFuture(null);
        });
        return this;
    }
    
    public List<DynamicAsyncStep> getSteps() {
        return steps;
    }
}

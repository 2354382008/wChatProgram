package com.wChartProgram.buss.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors; /**
 * 流程执行器
 */
public class DynamicFlowExecutor {
    private ExecutorService executor = Executors.newFixedThreadPool(10);
    
    public CompletableFuture<Void> execute(DynamicFlowConfig config, DynamicApprovalContext context) {
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
        
        for (DynamicAsyncStep step : config.getSteps()) {
            future = future.thenCompose(v -> {
                if (!context.isApproved()) {
                    return CompletableFuture.completedFuture(null);
                }
                return step.execute(context);
            });
        }
        
        return future.whenComplete((v, e) -> {
            if (e != null) {
                context.setApproved(false);
                context.setRejectReason("流程执行异常: " + e.getMessage());
                System.err.println("流程异常: " + e.getMessage());
            }
        });
    }
}

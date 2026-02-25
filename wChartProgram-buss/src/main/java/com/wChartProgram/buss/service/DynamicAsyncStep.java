package com.wChartProgram.buss.service;

import java.util.concurrent.CompletableFuture; /**
 * 异步流程步骤接口
 */
@FunctionalInterface
public interface DynamicAsyncStep {
    CompletableFuture<Void> execute(DynamicApprovalContext context);
}

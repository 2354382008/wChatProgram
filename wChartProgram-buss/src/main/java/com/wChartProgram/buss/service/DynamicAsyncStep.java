package com.wChartProgram.buss.service;

import java.util.concurrent.CompletableFuture;

/**
 * 异步流程步骤接口
 * @author wangmq
 */
@FunctionalInterface
public interface DynamicAsyncStep {

    /**
     * 异步节点执行逻辑
     * @param context
     * @return
     */
    CompletableFuture<Void> execute(DynamicApprovalContext context);
}

package com.wChartProgram.buss.service;

/**
 * 同步流程步骤接口,定义同步节点的执行逻辑
 * @author weangmq
 */
@FunctionalInterface
public interface DynamicSyncStep {

    /**
     * 同步节点执行逻辑
     * @param context
     */
    void execute(DynamicApprovalContext context);
}

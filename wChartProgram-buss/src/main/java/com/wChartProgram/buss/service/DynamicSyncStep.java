package com.wChartProgram.buss.service;

// 同步流程步骤接口
@FunctionalInterface
public interface DynamicSyncStep {
    void execute(DynamicApprovalContext context);
}

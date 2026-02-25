package com.wChartProgram.buss.service;

/**
 * 风控回调处理器
 */
public interface RiskCallbackHandler {
    void onRiskResult(String flowId, RiskControlType type, String result);
}

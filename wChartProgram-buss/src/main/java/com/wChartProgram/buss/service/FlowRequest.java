package com.wChartProgram.buss.service;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 流程请求参数
 * 优化点：标准化的流程请求参数类
 * @author wangmq
 */
@Data
public class FlowRequest {

    /**
     * 流程ID（可选，不传则自动生成）
     */
    private String flowId;

    /**
     * 申请编号
     */
    private String applyNo;

    /**
     * 流程类型（如：credit-授信, loan-放款, repayment-还款等）
     */
    private String flowType;

    /**
     * 风控执行顺序（可选，不传则使用默认配置）
     */
    private List<RiskControlType> riskControlOrder;

    /**
     * 流程初始数据（可选）
     */
    private Map<String, Object> data;

    /**
     * 流程超时时间（秒，可选）
     */
    private Integer timeoutSeconds;

    /**
     * 业务参数（扩展字段，可存储任意业务相关的参数）
     */
    private Map<String, Object> businessParams;
}

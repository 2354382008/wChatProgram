package com.wChartProgram.buss.service;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger; /**
 * 授信流程上下文
 */
@Data
public class DynamicApprovalContext{

    private String applyNo;
    private String flowId;
    private Map<String, Object> data = new HashMap<>();
    private boolean approved = true;
    private String rejectReason;

    /**
     * 风控执行顺序
     */
    private List<RiskControlType> riskControlOrder;

    /**
     * 风控结果
     */
    private Map<RiskControlType, String> riskResults = new HashMap<>();

    /**
     * 用于等待风控回调
     */
    private CountDownLatch riskLatch;

    /**
     * 当前执行的风控步骤
     */
    private AtomicInteger currentRiskStep = new AtomicInteger(0);

    /**
     * 风控流程的Future
     */
    private CompletableFuture<Void> riskFuture;

}

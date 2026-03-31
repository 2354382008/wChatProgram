package com.wChartProgram.buss.service;

import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 授信流程上下文（优化版本）
 * 优化点：
 * 1. 添加流程类型字段，支持多种流程类型
 * 2. 添加时间戳字段，便于监控和审计
 * 3. 添加流程状态字段，支持状态查询
 * 4. 支持业务参数扩展
 *
 * @author wangmq
 */
@Data
public class DynamicApprovalContext {

    /**
     * 申请编号
     */
    private String applyNo;

    /**
     * 流程ID
     */
    private String flowId;

    /**
     * 流程类型（如：credit-授信, loan-放款, repayment-还款等）
     * 优化点：支持多种流程类型
     */
    private String flowType;

    /**
     * 流程数据
     */
    private Map<String, Object> data = new HashMap<>();

    /**
     * 是否审批通过
     */
    private boolean approved = true;

    /**
     * 拒绝原因
     */
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

    /**
     * 流程开始时间
     * 优化点：记录流程开始时间，便于计算耗时
     */
    private Date startTime;

    /**
     * 流程结束时间
     * 优化点：记录流程结束时间，便于审计
     */
    private Date endTime;

    /**
     * 流程状态：PROCESSING-处理中, COMPLETED-已完成, FAILED-失败, TIMEOUT-超时
     * 优化点：添加流程状态字段，支持状态查询
     */
    private String status;

    /**
     * 业务参数（扩展字段，可存储任意业务相关的参数）
     * 优化点：支持业务参数扩展
     */
    private Map<String, Object> businessParams = new HashMap<>();

    /**
     * 当前执行的步骤
     * 优化点：记录当前执行的步骤，便于监控
     */
    private String currentStep;

    /**
     * 流程总步骤数
     * 优化点：记录总步骤数，便于计算进度
     */
    private int totalSteps;

    /**
     * 已完成的步骤数
     * 优化点：记录已完成步骤数，便于计算进度
     */
    private AtomicInteger completedSteps = new AtomicInteger(0);
}

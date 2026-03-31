package com.wChartProgram.buss.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 动态风控流程实现（优化版本）
 * 优化点：
 * 1. 支持动态节点配置，可从配置中心加载流程节点配置
 * 2. 提供公共流程入口，支持多种流程类型
 * 3. 集成持久化存储、分布式支持、配置中心集成、监控告警等功能
 * 4. 灵活的风控顺序配置，支持运行时调整
 * 5. 完善的异常处理和超时控制
 *
 * @author wangmq
 */
@Service
public class DynamicRiskControlFlow {

    @Autowired
    private FlowService flowService;

    /**
     * 传统流程入口（保持兼容性）
     * @deprecated 建议使用 FlowService.executeFlow() 方法
     */
    @Deprecated
    public void flowHandle() {
        System.out.println("========================================");
        System.out.println("使用传统流程入口（建议升级到 FlowService）");
        System.out.println("========================================");

        // 创建流程请求
        FlowRequest request = new FlowRequest();
        request.setApplyNo("APPLY_123456");
        request.setFlowType("credit"); // 授信流程

        // 优化点：动态设置风控顺序
        // 示例1: 京发风控 -> 资金方风控
        request.setRiskControlOrder(Arrays.asList(RiskControlType.JINGFA, RiskControlType.FUNDER));

        // 示例2: 资金方风控 -> 京发风控（动态调整顺序）
        // request.setRiskControlOrder(Arrays.asList(RiskControlType.FUNDER, RiskControlType.JINGFA));

        // 执行流程
        FlowResult result = flowService.executeFlow(request);

        // 输出结果
        System.out.println("\n========================================");
        System.out.println("流程执行完成:");
        System.out.println("流程ID: " + result.getFlowId());
        System.out.println("申请人ID: " + result.getApplyNo());
        System.out.println("是否成功: " + (result.isSuccess() ? "是" : "否"));
        System.out.println("最终结果: " + (result.isApproved() ? "通过" : "拒绝"));
        if (!result.isApproved()) {
            System.out.println("拒绝原因: " + result.getRejectReason());
        }
        if (result.getDuration() > 0) {
            System.out.println("执行耗时: " + result.getDuration() + "ms");
        }
        System.out.println("========================================");
    }

    /**
     * 推荐使用的公共流程入口
     * 优化点：提供标准化的流程执行接口
     * @param applyNo 申请编号
     * @return 流程执行结果
     */
    public FlowResult executeCreditFlow(String applyNo) {
        FlowRequest request = new FlowRequest();
        request.setApplyNo(applyNo);
        request.setFlowType("credit");

        // 优化点：使用默认风控顺序，或从配置中心读取
        // 实际应用中应该根据业务需求动态设置
        request.setRiskControlOrder(Arrays.asList(RiskControlType.JINGFA, RiskControlType.FUNDER));

        return flowService.executeFlow(request);
    }

    /**
     * 异步执行流程（不阻塞调用方）
     * 优化点：提供异步执行接口
     * @param applyNo 申请编号
     * @return CompletableFuture
     */
    public CompletableFuture<FlowResult> executeCreditFlowAsync(String applyNo) {
        FlowRequest flowRequest = new FlowRequest();
        flowRequest.setApplyNo(applyNo);
        // Generate and set the flowId
        flowRequest.setFlowId(generateFlowId());
        return flowService.executeFlowAsync(flowRequest);
    }

    /**
     * Generates a unique flow ID.
     * @return A unique flow ID string.
     */
    private String generateFlowId() {
        return "FLOW_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
    }

    /**
     * 根据流程类型执行流程
     * 优化点：支持多种流程类型
     * @param flowType 流程类型（如：credit-授信, loan-放款, repayment-还款等）
     * @param applyNo 申请编号
     * @return 流程执行结果
     */
    public FlowResult executeFlowByType(String flowType, String applyNo) {
        return flowService.executeFlowByType(flowType, applyNo);
    }

    /**
     * 自定义风控顺序执行流程
     * 优化点：支持运行时动态调整风控顺序
     * @param applyNo 申请编号
     * @param riskControlOrder 风控执行顺序
     * @return 流程执行结果
     */
    public FlowResult executeFlowWithCustomRiskOrder(String applyNo, java.util.List<RiskControlType> riskControlOrder) {
        FlowRequest request = new FlowRequest();
        request.setApplyNo(applyNo);
        request.setFlowType("credit");
        request.setRiskControlOrder(riskControlOrder);

        return flowService.executeFlow(request);
    }

    /**
     * 查询流程状态
     * 优化点：提供流程状态查询接口
     * @param flowId 流程ID
     * @return 流程状态
     */
    public FlowStatus queryFlowStatus(String flowId) {
        return flowService.getFlowStatus(flowId);
    }
}

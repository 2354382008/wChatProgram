package com.wChartProgram.buss.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 动态风控流程使用示例
 * 优化点：展示优化后的动态风控流程的各种使用方式
 *
 * @author wangmq
 */
@Slf4j
@Component
public class DynamicRiskControlFlowExample {

    @Autowired
    private DynamicRiskControlFlow dynamicRiskControlFlow;

    @Autowired
    private FlowService flowService;

    @Autowired
    private FlowNodeRegistry nodeRegistry;

    /**
     * 示例1：使用传统方式执行流程（保持兼容性）
     * 优化点：保持向后兼容，同时推荐使用新的 FlowService
     */
    public void example1_TraditionalFlow() {
        System.out.println("\n========== 示例1：传统流程执行 ==========");
        dynamicRiskControlFlow.flowHandle();
    }

    /**
     * 示例2：使用推荐的公共流程入口
     * 优化点：使用标准化的 FlowService 接口
     */
    public void example2_RecommendedFlowEntry() {
        System.out.println("\n========== 示例2：推荐流程入口 ==========");

        String applyNo = "APPLY_" + System.currentTimeMillis();
        FlowResult result = dynamicRiskControlFlow.executeCreditFlow(applyNo);

        System.out.println("流程执行结果:");
        System.out.println("Flow ID: " + result.getFlowId());
        System.out.println("Apply No: " + result.getApplyNo());
        System.out.println("Success: " + result.isSuccess());
        System.out.println("Approved: " + result.isApproved());
        if (!result.isApproved()) {
            System.out.println("Reject Reason: " + result.getRejectReason());
        }
    }

    /**
     * 示例3：异步执行流程
     * 优化点：不阻塞调用方，适合长时间运行的流程
     */
    public void example3_AsyncFlow() {
        System.out.println("\n========== 示例3：异步流程执行 ==========");

        String applyNo = "APPLY_" + System.currentTimeMillis();
        CompletableFuture<FlowResult> future = dynamicRiskControlFlow.executeCreditFlowAsync(applyNo);

        // 不等待，继续执行其他逻辑
        System.out.println("流程已异步启动，Apply No: " + applyNo);

        // 其他业务逻辑...

        // 等待结果（可选）
        try {
            FlowResult result = future.get();
            System.out.println("异步流程执行结果: " + result.isSuccess());
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("等待异步流程结果时出错: " + e.getMessage());
        }
    }

    /**
     * 示例4：根据流程类型执行
     * 优化点：支持多种流程类型
     */
    public void example4_ExecuteByFlowType() {
        System.out.println("\n========== 示例4：按流程类型执行 ==========");

        // 授信流程
        FlowResult creditResult = dynamicRiskControlFlow.executeFlowByType("credit", "CREDIT_001");
        System.out.println("授信流程结果: " + creditResult.isApproved());

        // 放款流程
        FlowResult loanResult = dynamicRiskControlFlow.executeFlowByType("loan", "LOAN_001");
        System.out.println("放款流程结果: " + loanResult.isApproved());

        // 还款流程
        FlowResult repaymentResult = dynamicRiskControlFlow.executeFlowByType("repayment", "REPAY_001");
        System.out.println("还款流程结果: " + repaymentResult.isApproved());
    }

    /**
     * 示例5：自定义风控顺序
     * 优化点：支持运行时动态调整风控顺序
     */
    public void example5_CustomRiskOrder() {
        System.out.println("\n========== 示例5：自定义风控顺序 ==========");

        String applyNo = "APPLY_" + System.currentTimeMillis();

        // 示例1：京发风控在前
        List<RiskControlType> order1 = Arrays.asList(RiskControlType.JINGFA, RiskControlType.FUNDER);
        FlowResult result1 = dynamicRiskControlFlow.executeFlowWithCustomRiskOrder(applyNo + "_1", order1);
        System.out.println("京发在前结果: " + result1.isApproved());

        // 示例2：资金方风控在前
        List<RiskControlType> order2 = Arrays.asList(RiskControlType.FUNDER, RiskControlType.JINGFA);
        FlowResult result2 = dynamicRiskControlFlow.executeFlowWithCustomRiskOrder(applyNo + "_2", order2);
        System.out.println("资金方在前结果: " + result2.isApproved());
    }

    /**
     * 示例6：查询流程状态
     * 优化点：支持流程状态查询
     */
    public void example6_QueryFlowStatus() {
        System.out.println("\n========== 示例6：查询流程状态 ==========");

        String flowId = "FLOW_TEST_12345";
        FlowStatus status = dynamicRiskControlFlow.queryFlowStatus(flowId);

        System.out.println("流程状态:");
        System.out.println("Flow ID: " + status.getFlowId());
        System.out.println("Status: " + status.getStatus());
        System.out.println("Approved: " + status.isApproved());
        System.out.println("Progress: " + status.getProgress() + "%");
        if (!status.isApproved()) {
            System.out.println("Reject Reason: " + status.getRejectReason());
        }
    }

    /**
     * 示例7：使用 FlowService 直接执行
     * 优化点：使用 FlowService 提供的更多功能
     */
    public void example7_FlowServiceDirect() {
        System.out.println("\n========== 示例7：FlowService 直接执行 ==========");

        // 创建流程请求
        FlowRequest request = new FlowRequest();
        request.setApplyNo("APPLY_" + System.currentTimeMillis());
        request.setFlowType("credit");

        // 设置自定义参数
        Map<String, Object> map = Collections.unmodifiableMap(
                new HashMap<String, Object>() {{
                    put("customerType", "VIP");
                    put("amount", 10000);
                }}
        );
        request.setData(map);

        // 设置风控顺序
        request.setRiskControlOrder(Arrays.asList(RiskControlType.JINGFA, RiskControlType.FUNDER));

        // 执行流程
        FlowResult result = flowService.executeFlow(request);

        System.out.println("流程执行结果: " + result.isSuccess() + ", Approved: " + result.isApproved());
    }

    /**
     * 示例8：动态管理流程节点
     * 优化点：运行时动态启用/禁用节点
     */
    public void example8_DynamicNodeManagement() {
        System.out.println("\n========== 示例8：动态节点管理 ==========");

        // 查看所有节点配置
        System.out.println("当前所有节点配置:");
        nodeRegistry.getAllNodeConfigs().forEach((nodeId, config) -> {
            System.out.println("  - " + nodeId + ": " + config.getNodeName() +
                    ", Enabled: " + config.isEnabled() + ", Order: " + config.getOrder());
        });

        // 禁用某个节点
        nodeRegistry.updateNodeEnabled("creditRuleValidation", false);
        System.out.println("已禁用节点: creditRuleValidation");

        // 启用某个节点
        nodeRegistry.updateNodeEnabled("creditRuleValidation", true);
        System.out.println("已启用节点: creditRuleValidation");

        // 注册自定义节点
        nodeRegistry.registerSyncNode("customNode", context -> {
            System.out.println("执行自定义节点...");
            context.getData().put("customResult", "passed");
        });

        // 注册节点配置
        FlowNodeConfig customConfig = FlowNodeConfig.createSyncNode("customNode", "自定义节点", 7);
        nodeRegistry.registerNodeConfig(customConfig);

        System.out.println("已注册自定义节点: customNode");
    }

    /**
     * 示例9：完整流程演示
     * 优化点：展示完整的流程使用场景
     */
    public void example9_CompleteFlowDemo() {
        System.out.println("\n========== 示例9：完整流程演示 ==========");

        // 步骤1：创建申请
        String applyNo = "APPLY_" + System.currentTimeMillis();
        System.out.println("创建申请: " + applyNo);

        // 步骤2：启动流程
        System.out.println("启动流程...");
        FlowRequest request = new FlowRequest();
        request.setApplyNo(applyNo);
        request.setFlowType("credit");
        request.setRiskControlOrder(Arrays.asList(RiskControlType.JINGFA, RiskControlType.FUNDER));
        FlowResult result = flowService.executeFlow(request);

        // 步骤3：查看结果
        System.out.println("\n流程执行结果:");
        System.out.println("Flow ID: " + result.getFlowId());
        System.out.println("Apply No: " + result.getApplyNo());
        System.out.println("Success: " + result.isSuccess());
        System.out.println("Approved: " + result.isApproved());

        // 步骤4：如果通过，查看详情
        if (result.isApproved()) {
            System.out.println("\n流程数据:");
            result.getData().forEach((key, value) -> {
                System.out.println("  " + key + ": " + value);
            });
        } else {
            System.out.println("\n拒绝原因: " + result.getRejectReason());
        }
    }

    /**
     * 主入口方法：运行所有示例
     */
    public void runAllExamples() {
        System.out.println("========================================");
        System.out.println("动态风控流程示例集合");
        System.out.println("========================================");

        // 可以选择性地运行示例
        example2_RecommendedFlowEntry();
        example3_AsyncFlow();
        example4_ExecuteByFlowType();
        example5_CustomRiskOrder();
        example7_FlowServiceDirect();
        example8_DynamicNodeManagement();
        example9_CompleteFlowDemo();

        System.out.println("\n========================================");
        System.out.println("所有示例执行完成");
        System.out.println("========================================");
    }
}

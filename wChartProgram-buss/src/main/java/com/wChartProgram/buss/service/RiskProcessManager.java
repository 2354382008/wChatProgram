package com.wChartProgram.buss.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit; /**
 * 风控流程管理器（核心类，处理风控顺序和回调）
 */
@Service
public class RiskProcessManager implements RiskCallbackHandler {

    private Map<String, DynamicApprovalContext> contextMap = new ConcurrentHashMap<>();
    private RiskControlClient riskClient;
    
    public RiskProcessManager() {
        this.riskClient = new RiskControlClient(this);
    }

    /**
     * 开始风控流程
     * @param context
     * @return
     */
    public CompletableFuture<Void> startRiskProcess(DynamicApprovalContext context) {
        // 存储上下文，用于回调时查找
        contextMap.put(context.getFlowId(), context);
        // 创建Future用于等待风控完成
        CompletableFuture<Void> future = new CompletableFuture<>();
        // 初始化计数器，用于等待所有风控步骤完成
        context.setRiskLatch(new CountDownLatch(context.getRiskControlOrder().size()));
        context.setRiskFuture(future);
        // 启动第一个风控
        startNextRiskControl(context);
        //异步校验风控线程时间是否超时
        CompletableFuture.runAsync(() -> {
            try {
                if (!context.getRiskLatch().await(30, TimeUnit.SECONDS)) {
                    context.setApproved(false);
                    context.setRejectReason("风控流程超时");
                    context.getRiskFuture().complete(null);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        return future;
    }

    /**
     * 启动下一个风控
     * @param context
     */
    private void startNextRiskControl(DynamicApprovalContext context) {
        List<RiskControlType> order = context.getRiskControlOrder();
        int currentStep = context.getCurrentRiskStep().get();
        
        if (currentStep < order.size()) {
            RiskControlType nextRiskType = order.get(currentStep);
            System.out.println("开始第" + (currentStep + 1) + "道风控: " + nextRiskType.getDescription() + 
                               " - flowId: " + context.getFlowId());
            riskClient.startRiskControl(context.getFlowId(), nextRiskType);
        }
    }

    /**
     * 处理风控回调结果
     * @param flowId
     * @param type
     * @param result
     */
    @Override
    public void onRiskResult(String flowId, RiskControlType type, String result) {
        DynamicApprovalContext context = contextMap.get(flowId);
        if (context == null) {
            System.err.println("未找到对应的流程上下文 - flowId: " + flowId);
            return;
        }
        
        try {
            System.out.println(type.getDescription() + "回调结果: " + result + " - flowId: " + flowId);
            context.getRiskResults().put(type, result);
            
            // 检查当前风控是否通过
            if (!"passed".equals(result)) {
                context.setApproved(false);
                context.setRejectReason(type.getDescription() + "未通过");
                // 完成Future，结束风控流程
                context.getRiskFuture().complete(null);
                return;
            }
            
            // 移动到下一步
            int nextStep = context.getCurrentRiskStep().incrementAndGet();
            context.getRiskLatch().countDown();
            
            // 如果还有下一个风控，启动它
            if (nextStep < context.getRiskControlOrder().size()) {
                startNextRiskControl(context);
            } else {
                // 所有风控完成
                System.out.println("所有风控步骤完成 - flowId: " + flowId);
                context.getRiskFuture().complete(null);
            }
        } catch (Exception e) {
            context.getRiskFuture().completeExceptionally(e);
        }
    }
}

package com.wChartProgram.buss.service;

import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 风控服务客户端（负责发起风控请求）
 * @author wangmq
 */
public class RiskControlClient {

    private ExecutorService executor = Executors.newCachedThreadPool();
    private RiskCallbackHandler callbackHandler;
    
    public RiskControlClient(RiskCallbackHandler callbackHandler) {
        this.callbackHandler = callbackHandler;
    }

    /**
     * 发起风控请求
     * @param flowId
     * @param type
     */
    public void startRiskControl(String flowId, RiskControlType type) {
        executor.submit(() -> {
            try {
                // 模拟调用远程风控服务
                System.out.println("调用" + type.getDescription() + "服务 - flowId: " + flowId);
                
                // 实际项目中这里会是真实的远程调用

                // todo 暂时写在此处 作用：远程服务处理完成后会调用我们提供的回调接口
                simulateRemoteRiskProcessing(flowId, type);
            } catch (Exception e) {
                System.err.println(type.getDescription() + "调用失败: " + e.getMessage());
            }
        });
    }

    /**
     * 模拟远程风控处理（实际中不需要，这里用于演示）
     * @param flowId
     * @param type
     * @throws InterruptedException
     */
    private void simulateRemoteRiskProcessing(String flowId, RiskControlType type) throws InterruptedException {
        // 模拟处理时间
        Thread.sleep(type == RiskControlType.JINGFA ? 1000 : 1500);
        
        // 模拟风控结果（实际中由远程服务调用回调接口）
        String result = "passed"; // 或 "rejected"
        callbackHandler.onRiskResult(flowId, type, result);
    }
}

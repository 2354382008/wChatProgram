package com.wChartProgram.buss.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 风控管理器（单例模式）
 * 优化点：使用单例模式管理风控流程，支持分布式场景
 * @author wangmq
 */
@Slf4j
@Component
public class RiskControlManager implements RiskCallbackHandler {

    /**
     * 单例实例
     */
    private static volatile RiskControlManager instance;

    /**
     * 上下文存储
     * 优化点：内存存储上下文，实际应用中应使用 Redis 等分布式缓存
     */
    private Map<String, DynamicApprovalContext> contextMap = new ConcurrentHashMap<>();

    /**
     * 风控客户端
     */
    private RiskControlClient riskClient;

    /**
     * 私有构造方法
     */
    private RiskControlManager() {
        this.riskClient = new RiskControlClient(this);
    }

    /**
     * 获取单例实例（双重检查锁）
     * 优化点：线程安全的单例实现
     */
    public static RiskControlManager getInstance() {
        if (instance == null) {
            synchronized (RiskControlManager.class) {
                if (instance == null) {
                    instance = new RiskControlManager();
                }
            }
        }
        return instance;
    }

    /**
     * 开始风控流程
     * 优化点：添加超时控制和分布式支持
     * @param context 流程上下文
     * @return CompletableFuture
     */
    public CompletableFuture<Void> startRiskProcess(DynamicApprovalContext context) {
        // 优化点：持久化存储流程上下文
        // 实际应用中应该保存到数据库，支持系统重启后恢复流程
        saveContextToDatabase(context);

        // 存储上下文，用于回调时查找
        contextMap.put(context.getFlowId(), context);

        // 优化点：使用分布式存储替代内存存储
        // 实际应用中应该使用 Redis 或其他分布式缓存
        // redisTemplate.opsForValue().set("flow:context:" + context.getFlowId(), context, timeout, TimeUnit.SECONDS);

        //创建Future用于等待风控完成
        CompletableFuture<Void> future = new CompletableFuture<>();

        // 初始化计数器，用于等待所有风控步骤完成
        context.setRiskLatch(new CountDownLatch(context.getRiskControlOrder().size()));
        context.setRiskFuture(future);

        // 启动第一个风控
        startNextRiskControl(context);

        // 优化点：添加超时控制
        // 优化点：添加监控告警，对长时间未回调的风控流程添加告警机制
        CompletableFuture.runAsync(() -> {
            try {
                // 优化点：超时时间应该可配置，建议从配置中心读取
                long timeoutSeconds = getRiskControlTimeout();
                if (!context.getRiskLatch().await(timeoutSeconds, TimeUnit.SECONDS)) {
                    // 优化点：记录超时事件到监控系统
                    logRiskControlTimeout(context);

                    context.setApproved(false);
                    context.setRejectReason("风控流程超时");
                    context.getRiskFuture().complete(null);

                    // 优化点：触发告警通知
                    sendRiskControlTimeoutAlert(context);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // 优化点：记录中断异常
                logInterruptedException(e, context);
            }
        });

        return future;
    }

    /**
     * 启动下一个风控
     * @param context 流程上下文
     */
    private void startNextRiskControl(DynamicApprovalContext context) {
        List<RiskControlType> order = context.getRiskControlOrder();
        int currentStep = context.getCurrentRiskStep().get();
        if (currentStep < order.size()) {
            RiskControlType nextRiskType = order.get(currentStep);
            System.out.println("开始第" + (currentStep + 1) + "道风控: " + nextRiskType.getDescription() +
                    " - flowId: " + context.getFlowId());
            // 优化点：记录风控开始事件到监控系统
            logRiskControlStart(context, nextRiskType);
            riskClient.startRiskControl(context.getFlowId(), nextRiskType);
        }
    }

    /**
     * 处理风控回调结果
     * 优化点：添加详细的日志记录和异常处理
     * @param flowId 流程ID
     * @param type 风控类型
     * @param result 风控结果
     */
    @Override
    public void onRiskResult(String flowId, RiskControlType type, String result) {
        DynamicApprovalContext context = contextMap.get(flowId);
        if (context == null) {
            System.err.println("未找到对应的流程上下文 - flowId: " + flowId);
            // 优化点：记录上下文未找到的异常
            logContextNotFound(flowId);
            return;
        }

        try {
            // 优化点：记录风控结果到监控系统
            logRiskControlResult(context, type, result);

            System.out.println(type.getDescription() + "回调结果: " + result + " - flowId: " + flowId);
            context.getRiskResults().put(type, result);

            // 优化点：更新数据库中的流程状态
            updateRiskResultInDatabase(context, type, result);

            // 检查当前风控是否通过
            if (!"passed".equals(result)) {
                // 优化点：记录风控失败事件到监控系统
                logRiskControlFailed(context, type);

                context.setApproved(false);
                context.setRejectReason(type.getDescription() + "未通过");

                // 优化点：触发风控失败告警
                sendRiskControlFailedAlert(context, type);

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

                // 优化点：记录风控完成事件到监控系统
                logRiskControlCompleted(context);

                // 优化点：更新数据库中的流程状态为完成
                updateFlowStatusToCompleted(context);

                context.getRiskFuture().complete(null);
            }
        } catch (Exception e) {
            // 优化点：记录异常到监控系统
            logRiskControlException(e, context);

            context.getRiskFuture().completeExceptionally(e);
        }
    }

    /**
     * 优化点：持久化存储流程上下文到数据库
     * 实际实现示例：
     */
    private void saveContextToDatabase(DynamicApprovalContext context) {
        log.info("持久化存储流程到上下文数据库");
    }

    /**
     * 优化点：更新风控结果到数据库
     * 实际实现示例：
     */
    private void updateRiskResultInDatabase(DynamicApprovalContext context, RiskControlType type, String result) {
        log.info("更新风控结果");
    }

    /**
     * 优化点：更新流程状态为完成
     * 实际实现示例：
     */
    private void updateFlowStatusToCompleted(DynamicApprovalContext context) {
        log.info("更新流程完成状态");
    }

    /**
     * 优化点：获取风控超时时间
     * 实际实现示例：
     */
    private long getRiskControlTimeout() {
        // 默认30秒
        return 30;
    }

    /**
     * 优化点：记录风控超时事件
     * 实际实现示例：
     */
    private void logRiskControlTimeout(DynamicApprovalContext context) {
        log.info("调用风控超时");
    }

    /**
     * 优化点：发送风控超时告警
     * 实际实现示例：
     */
    private void sendRiskControlTimeoutAlert(DynamicApprovalContext context) {
        log.info("风控超时触发告警");
    }

    /**
     * 优化点：记录风控开始事件
     * 实际实现示例：
     */
    private void logRiskControlStart(DynamicApprovalContext context, RiskControlType type) {
        log.info("记录风控开始事件");
    }

    /**
     * 优化点：记录风控结果事件
     * 实际实现示例：
     */
    private void logRiskControlResult(DynamicApprovalContext context, RiskControlType type, String result) {
        log.info("风控结果");
    }

    /**
     * 优化点：记录风控失败事件
     * 实际实现示例：
     */
    private void logRiskControlFailed(DynamicApprovalContext context, RiskControlType type) {
        log.info("风控失败");
    }

    /**
     * 优化点：发送风控失败告警
     * 实际实现示例：
     */
    private void sendRiskControlFailedAlert(DynamicApprovalContext context, RiskControlType type) {
        log.info("风控失败告警");
    }

    /**
     * 优化点：记录风控完成事件
     * 实际实现示例：
     */
    private void logRiskControlCompleted(DynamicApprovalContext context) {
        log.info("风控完成");
    }

    /**
     * 优化点：记录上下文未找到异常
     * 实际实现示例：
     */
    private void logContextNotFound(String flowId) {
        log.info("上下文未找到异常");
    }

    /**
     * 优化点：记录中断异常
     * 实际实现示例：
     */
    private void logInterruptedException(InterruptedException e, DynamicApprovalContext context) {
        log.info("风控中断");
    }

    /**
     * 优化点：记录风控异常
     * 实际实现示例：
     */
    private void logRiskControlException(Exception e, DynamicApprovalContext context) {
        log.info("风控异常");
    }
}

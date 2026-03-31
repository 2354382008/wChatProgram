package com.wChartProgram.buss.service;

import com.wChartProgram.common.util.FutureTimeoutUtil;
import com.wChartProgram.common.util.FutureUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static reactor.core.publisher.MonoWhenFunctionsKt.whenComplete;

/**
 * 流程执行器（优化版本）
 * 优化点：
 * 1. 支持流程超时控制
 * 2. 支持步骤执行时间统计
 * 3. 完善的异常处理和日志记录
 * 4. 支持流程进度跟踪
 *
 * @author wangmq
 */
@Slf4j
public class DynamicFlowExecutor {

    /**
     * 线程池（使用固定大小的线程池）
     */
    private ExecutorService executor = Executors.newFixedThreadPool(10);

    /**
     * 执行流程
     * 优化点：添加超时控制和异常处理
     * @param config 流程配置
     * @param context 流程上下文
     * @return CompletableFuture
     */
    public CompletableFuture<Void> execute(DynamicFlowConfig config, DynamicApprovalContext context) {
        // 优化点：设置流程开始时间
        context.setStartTime(new java.util.Date());
        context.setStatus("PROCESSING");
        context.setTotalSteps(config.getStepCount());

        System.out.println("开始执行流程: " + config.getFlowName());
        System.out.println("流程描述: " + config.getDescription());
        System.out.println("总步骤数: " + config.getStepCount());
        System.out.println("超时时间: " + config.getTimeoutSeconds() + "秒");

        // 校验配置
        if (!config.validate()) {
            context.setApproved(false);
            context.setRejectReason("流程配置无效");
            context.setEndTime(new java.util.Date());
            context.setStatus("FAILED");
            return FutureUtils.failedFuture(new IllegalArgumentException("流程配置无效"));
        }

        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);

        // 优化点：记录步骤开始执行
        final AtomicInteger stepIndex = new AtomicInteger(0);

        for (DynamicAsyncStep step : config.getSteps()) {
            final int currentStepIndex = stepIndex.incrementAndGet();
            future = future.thenCompose(v -> {
                // 优化点：检查是否已拒绝，提前终止流程
                if (!context.isApproved()) {
                    System.out.println("流程已被拒绝，停止执行后续步骤 - flowId: " + context.getFlowId());
                    return CompletableFuture.completedFuture(null);
                }

                // 优化点：记录当前步骤
                context.setCurrentStep("步骤" + currentStepIndex);

                long startTime = System.currentTimeMillis();

                System.out.println("开始执行步骤" + currentStepIndex + "/" + config.getStepCount() +
                        " - flowId: " + context.getFlowId());

                try {
                    // 执行步骤
                    return step.execute(context).whenComplete((result, exception) -> {
                        long duration = System.currentTimeMillis() - startTime;
                        System.out.println("步骤" + currentStepIndex + "执行完成，耗时: " + duration + "ms" +
                                " - flowId: " + context.getFlowId());

                        // 优化点：记录步骤完成
                        context.getCompletedSteps().incrementAndGet();

                        // 优化点：记录步骤执行时间（实际应用中应该记录到监控系统）
                        logStepExecution(currentStepIndex, duration, exception);

                    });
                } catch (Exception e) {
                    // 优化点：捕获步骤执行异常
                    long duration = System.currentTimeMillis() - startTime;
                    System.err.println("步骤" + currentStepIndex + "执行异常，耗时: " + duration + "ms" +
                            " - flowId: " + context.getFlowId());
                    System.err.println("异常信息: " + e.getMessage());

                    // 优化点：记录异常（实际应用中应该记录到监控系统）
                    logStepException(currentStepIndex, e);

                    context.setApproved(false);
                    context.setRejectReason("步骤" + currentStepIndex + "执行异常: " + e.getMessage());
                    return FutureUtils.failedFuture(e);
                }
            });
        }
        //添加超时控制
        CompletableFuture<Void> timeoutFuture = FutureTimeoutUtil.orTimeout(future,5,TimeUnit.SECONDS)
                .whenComplete((v, e) -> {
                    context.setEndTime(new java.util.Date());
                    if (e instanceof java.util.concurrent.TimeoutException) {
                        // 优化点：处理超时异常
                        context.setApproved(false);
                        context.setRejectReason("流程执行超时");
                        context.setStatus("TIMEOUT");

                        System.err.println("流程执行超时 - flowId: " + context.getFlowId());

                        // 优化点：记录超时事件（实际应用中应该记录到监控系统）
                        logFlowTimeout(context);
                    } else if (e != null) {
                        // 优化点：处理其他异常
                        context.setApproved(false);
                        context.setRejectReason("流程执行异常: " + e.getMessage());
                        context.setStatus("FAILED");

                        System.err.println("流程执行异常 - flowId: " + context.getFlowId() + ", 异常: " + e.getMessage());

                        // 优化点：记录异常事件（实际应用中应该记录到监控系统）
                        logFlowException(context, e);
                    } else {
                        // 流程正常完成
                        context.setStatus("COMPLETED");

                        // 优化点：记录流程完成事件（实际应用中应该记录到监控系统）
                        logFlowCompleted(context);
                    }

                    System.out.println("流程执行完成 - flowId: " + context.getFlowId() +
                            ", 状态: " + context.getStatus() +
                            ", 结果: " + (context.isApproved() ? "通过" : "拒绝"));

                    if (!context.isApproved() && context.getRejectReason() != null) {
                        System.out.println("拒绝原因: " + context.getRejectReason());
                    }
                });

        return timeoutFuture;
    }

    /**
     * 关闭执行器
     * 优化点：提供资源释放方法
     */
    public void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 优化点：记录步骤执行时间
     * 实际实现示例：
     */
    private void logStepExecution(int stepIndex, long duration, Throwable exception) {
        log.info("记录步骤执行时间");
    }

    /**
     * 优化点：记录步骤异常
     * 实际实现示例：
     */
    private void logStepException(int stepIndex, Exception e) {
        log.info("记录步骤异常");
    }

    /**
     * 优化点：记录流程超时事件
     * 实际实现示例：
     */
    private void logFlowTimeout(DynamicApprovalContext context) {
        log.info("记录流程超时事件");
    }

    /**
     * 优化点：记录流程异常事件
     * 实际实现示例：
     */
    private void logFlowException(DynamicApprovalContext context, Throwable e) {
         log.info("流程执行异常 - flowId: {}", context.getFlowId(), e);
    }

    /**
     * 优化点：记录流程完成事件
     * 实际实现示例：
     */
    private void logFlowCompleted(DynamicApprovalContext context) {
        log.info("测试");
    }
}

package com.wChartProgram.common.util;

import java.util.concurrent.*;

/**
 * Future 超时工具类
 * @author wangmq
 */
public class FutureTimeoutUtil {

    /**
     * 全局单例调度器
     */
    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);

    /**
     * 等价于 jdk 1.9 future.orTimeout(timeout, unit)
     */
    public static <T> CompletableFuture<T> orTimeout(CompletableFuture<T> future,long timeout,TimeUnit unit) {
        if (future.isDone()) {
            return future;
        }
        // 调度任务：超时后让 future 异常完成
        ScheduledFuture<?> timeoutTask = SCHEDULER.schedule(() -> {
            if (!future.isDone()) {
                future.completeExceptionally(new TimeoutException("任务超时"));
            }
        }, timeout, unit);

        // 任务正常完成时，取消超时任务，避免浪费
        future.whenComplete((res, ex) -> {
            timeoutTask.cancel(false);
        });
        return future;
    }
}
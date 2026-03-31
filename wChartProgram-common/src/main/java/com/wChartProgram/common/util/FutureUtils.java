package com.wChartProgram.common.util;

import java.util.concurrent.CompletableFuture;

/**
 * 获取异常信息
 * @author wangmq
 */
public class FutureUtils {

    /**
     * 等价于 Java 9+ 的 CompletableFuture.failedFuture(e)
     */
    public static <T> CompletableFuture<T> failedFuture(Throwable ex) {
        CompletableFuture<T> future = new CompletableFuture<>();
        // 手动标记为异常完成
        future.completeExceptionally(ex);
        return future;
    }
}
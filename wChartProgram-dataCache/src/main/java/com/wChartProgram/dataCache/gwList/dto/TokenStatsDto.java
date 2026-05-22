package com.wChartProgram.dataCache.gwList.dto;

import java.util.Map;

/**
 * @author 23543
 */
public record TokenStatsDto(
    double totalTokensM,      // 单位：M
    double promptTokensM,
    double completionTokensM,
    double cacheHitRate,
    Map<String, AppTokenStats> appStats
) {
    public record AppTokenStats(double totalM, double promptM, double completionM, double hitRate) {}
}
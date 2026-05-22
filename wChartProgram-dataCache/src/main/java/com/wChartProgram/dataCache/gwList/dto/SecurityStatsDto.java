package com.wChartProgram.dataCache.gwList.dto;

import java.util.List;
import java.util.Map;

/**
 * @author 23543
 */
public record SecurityStatsDto(
    double apikeyPassRate,
    long totalRequests,
    long successRequests,
    long apikeyAuthFailCount,
    long violationCallCount,
    long rateLimitCircuitCount,
    Map<String, HourlyTrend> hourlyTrends,
    List<SecurityRecord> records
) {
    public record HourlyTrend(String hour, long violationCount, long circuitCount) {}
    public record SecurityRecord(String appName, String type, String callTime, String clientIp, String detail) {}
}
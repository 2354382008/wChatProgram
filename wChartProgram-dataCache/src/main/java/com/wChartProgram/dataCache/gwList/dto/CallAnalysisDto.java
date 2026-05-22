package com.wChartProgram.dataCache.gwList.dto;

import java.util.Map;

/**
 * @author 23543
 */
public record CallAnalysisDto(
    long totalCalls,
    long successCalls,
    long failCalls,
    double successRate,
    Map<String, ClientStats> clientStats
) {
    public CallAnalysisDto() {
        this(0, 0, 0, 0.0, Map.of());
    }
    
    public record ClientStats(long total, long success, long fail, double rate) {}
}

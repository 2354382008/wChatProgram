package com.wChartProgram.dataCache.gwList.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MinuteAggDto {
    private String hourBucket;
    private String appId;
    private String clientId;
    private Long totalCalls;
    private Long successCalls;
    private Long failCalls;
    private Long totalTokens;
    private Long promptTokens;
    private Long completionTokens;
    private Long cacheHitTokens;
    private Long cacheMissTokens;
    private Long apikeyAuthFailCount;
    private Long violationCallCount;
    private Long rateLimitCount;
    private Long circuitBreakCount;
}

package com.wChartProgram.dataCache.gwList.dto;

/**
 * @author 23543
 */
public record GatewayLog(
    String requestId,
    String timestamp,
    String appId,
    String assetId,
    String assetType,
    Integer status,
    Boolean success,
    String errorCode,
    Integer durationMs,
    String clientId,
    UsageDto usageDto
) {}


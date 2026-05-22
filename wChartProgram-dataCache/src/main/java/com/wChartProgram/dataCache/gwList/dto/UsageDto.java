package com.wChartProgram.dataCache.gwList.dto;

/**
 * @author 23543
 */
public record UsageDto(
    Integer promptTokens,
    Integer completionTokens,
    Integer totalTokens,
    Integer cacheHitTokens,
    Integer cacheMissTokens,
    Double tokensPerSecond,
    Integer timeToFirstTokenMs,
    Double timePerOutputTokenMs
) {}
package com.wChartProgram.dataCache.gwList.service;

import com.wChartProgram.dataCache.gwList.dto.GatewayLog;
import com.wChartProgram.dataCache.gwList.dto.SecurityStatsDto;
import com.wChartProgram.dataCache.gwList.dto.UsageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

@Slf4j
@Component
public class RealtimeStatsAggregatorService {
    
    private static final DateTimeFormatter MINUTE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyyMMddHHmm");
    private static final int RETENTION_MINUTES = 60;
    
    private final Map<String, MinuteBucket> minuteBuckets = new ConcurrentHashMap<>();
    private final SecurityClassifier securityClassifier;
    
    public RealtimeStatsAggregatorService(SecurityClassifier securityClassifier) {
        this.securityClassifier = securityClassifier;
        startCleanupScheduler();
    }
    
    public void onLog(GatewayLog gatewayLog) {
        String minuteKey = getMinuteKey(gatewayLog.timestamp());
        Instant timestamp = parseTimestamp(gatewayLog.timestamp());
        MinuteBucket bucket = minuteBuckets.computeIfAbsent(minuteKey, 
            k -> new MinuteBucket(k, timestamp));
        
        bucket.totalCalls.increment();
        if (Boolean.TRUE.equals(gatewayLog.success())) {
            bucket.successCalls.increment();
        } else {
            bucket.failCalls.increment();
        }
        
        UsageDto usageDto = gatewayLog.usageDto();
        if (usageDto != null) {
            bucket.totalTokens.add(usageDto.totalTokens());
            bucket.promptTokens.add(usageDto.promptTokens() != null ? usageDto.promptTokens() : 0);
            bucket.completionTokens.add(usageDto.completionTokens() != null ? usageDto.completionTokens() : 0);
            bucket.cacheHitTokens.add(usageDto.cacheHitTokens() != null ? usageDto.cacheHitTokens() : 0);
            bucket.cacheMissTokens.add(usageDto.cacheMissTokens() != null ? usageDto.cacheMissTokens() : 0);
        }
        
        bucket.updateAppStats(gatewayLog);
        
        if (gatewayLog.clientId() != null) {
            bucket.updateClientStats(gatewayLog);
        }
        
        String securityType = securityClassifier.classify(gatewayLog);
        bucket.securityCounters.computeIfAbsent(securityType, k -> new LongAdder()).increment();
    }
    
    private Instant parseTimestamp(String timestampStr) {
        try {
            return Instant.parse(timestampStr);
        } catch (Exception e) {
            log.warn("解析时间戳失败: {}, 使用当前时间", timestampStr);
            return Instant.now();
        }
    }
    
    public CallAnalysisResult getCallAnalysis(Instant start, Instant end) {
        CallAnalysisResult result = new CallAnalysisResult();
        Map<String, ClientStatsResult> clientStatsMap = new ConcurrentHashMap<>();
        
        minuteBuckets.values().stream()
            .filter(bucket -> isInRange(bucket.getTimestamp(), start, end))
            .forEach(bucket -> {
                result.totalCalls += bucket.totalCalls.longValue();
                result.successCalls += bucket.successCalls.longValue();
                result.failCalls += bucket.failCalls.longValue();
                
                bucket.clientStats.forEach((clientId, stats) -> {
                    clientStatsMap.computeIfAbsent(clientId, k -> new ClientStatsResult())
                        .merge(stats);
                });
            });
        
        result.successRate = result.totalCalls > 0 ? 
            result.successCalls * 100.0 / result.totalCalls : 0;
        result.clientStats = clientStatsMap;
        
        return result;
    }
    
    public TokenStatsResult getTokenStats(Instant start, Instant end) {
        TokenStatsResult result = new TokenStatsResult();
        Map<String, AppTokenStatsResult> appStatsMap = new ConcurrentHashMap<>();
        
        minuteBuckets.values().stream()
            .filter(bucket -> isInRange(bucket.getTimestamp(), start, end))
            .forEach(bucket -> {
                result.totalTokens += bucket.totalTokens.longValue();
                result.promptTokens += bucket.promptTokens.longValue();
                result.completionTokens += bucket.completionTokens.longValue();
                result.cacheHitTokens += bucket.cacheHitTokens.longValue();
                result.cacheMissTokens += bucket.cacheMissTokens.longValue();
                
                bucket.appStats.forEach((appId, stats) -> {
                    appStatsMap.computeIfAbsent(appId, k -> new AppTokenStatsResult())
                        .merge(stats);
                });
            });
        
        result.cacheHitRate = (result.cacheHitTokens + result.cacheMissTokens) > 0 ?
            result.cacheHitTokens * 100.0 / (result.cacheHitTokens + result.cacheMissTokens) : 0;
        result.appStats = appStatsMap;
        
        return result;
    }
    
    public SecurityStatsResult getSecurityStats(Instant start, Instant end) {
        SecurityStatsResult result = new SecurityStatsResult();
        Map<String, HourlyTrend> hourlyTrends = new TreeMap<>();
        
        minuteBuckets.values().stream()
            .filter(bucket -> isInRange(bucket.getTimestamp(), start, end))
            .forEach(bucket -> {
                result.totalRequests += bucket.totalCalls.longValue();
                result.successRequests += bucket.successCalls.longValue();
                
                result.apikeyAuthFailCount += bucket.securityCounters
                    .getOrDefault("apikey_auth_fail", new LongAdder()).longValue();
                result.violationCallCount += bucket.securityCounters
                    .getOrDefault("违规调用", new LongAdder()).longValue();
                result.rateLimitCircuitCount += bucket.securityCounters
                    .getOrDefault("限流熔断", new LongAdder()).longValue();
                
                String hourKey = bucket.getTimestamp().truncatedTo(java.time.temporal.ChronoUnit.HOURS)
                    .toString().substring(0, 13);
                hourlyTrends.computeIfAbsent(hourKey, k -> new HourlyTrend()).add(bucket);
            });
        
        result.apikeyPassRate = result.totalRequests > 0 ?
            result.successRequests * 100.0 / result.totalRequests : 0;
        result.hourlyTrends = hourlyTrends;
        
        return result;
    }
    
    public List<SecurityStatsDto.SecurityRecord> getViolationDetails(Instant start, Instant end, int limit) {
        return new ArrayList<>();
    }
    
    private String getMinuteKey(String timestamp) {
        LocalDateTime localDateTime = ZonedDateTime.parse(timestamp).toLocalDateTime();
        return localDateTime.format(MINUTE_FORMATTER);
    }
    
    private boolean isInRange(Instant bucketTime, Instant start, Instant end) {
        return !bucketTime.isBefore(start) && !bucketTime.isAfter(end);
    }
    
    private void startCleanupScheduler() {
        Thread.ofVirtual().start(() -> {
            while (true) {
                try {
                    Thread.sleep(60000);
                    Instant expireTime = Instant.now().minusSeconds(RETENTION_MINUTES * 60L);
                    minuteBuckets.entrySet().removeIf(entry -> 
                        entry.getValue().getTimestamp().isBefore(expireTime));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }
    
    public static class CallAnalysisResult {
        public long totalCalls = 0;
        public long successCalls = 0;
        public long failCalls = 0;
        public double successRate = 0.0;
        public Map<String, ClientStatsResult> clientStats;
        
        public CallAnalysisResult() {
            this.clientStats = new ConcurrentHashMap<>();
        }
    }
    
    public static class ClientStatsResult {
        public long total = 0;
        public long success = 0;
        public long fail = 0;
        
        public void merge(MinuteBucket.ClientStats other) {
            this.total += other.getTotal();
            this.success += other.getSuccess();
            this.fail += other.getFail();
        }
    }
    
    public static class TokenStatsResult {
        public long totalTokens = 0;
        public long promptTokens = 0;
        public long completionTokens = 0;
        public long cacheHitTokens = 0;
        public long cacheMissTokens = 0;
        public double cacheHitRate = 0.0;
        public Map<String, AppTokenStatsResult> appStats;
        
        public TokenStatsResult() {
            this.appStats = new ConcurrentHashMap<>();
        }
    }
    
    public static class AppTokenStatsResult {
        public long totalTokens = 0;
        public long promptTokens = 0;
        public long completionTokens = 0;
        public double cacheHitRate = 0.0;
        
        public void merge(MinuteBucket.AppStats other) {
            this.totalTokens += other.getTotalTokens();
            this.promptTokens += other.getPromptTokens();
            this.completionTokens += other.getCompletionTokens();
        }
    }
    
    public static class SecurityStatsResult {
        public double apikeyPassRate = 0.0;
        public long totalRequests = 0;
        public long successRequests = 0;
        public long apikeyAuthFailCount = 0;
        public long violationCallCount = 0;
        public long rateLimitCircuitCount = 0;
        public Map<String, HourlyTrend> hourlyTrends;
        
        public SecurityStatsResult() {
            this.hourlyTrends = new TreeMap<>();
        }
    }
    
    public static class HourlyTrend {
        public long violationCount = 0;
        public long circuitCount = 0;
        
        public void add(MinuteBucket bucket) {
            this.violationCount += bucket.securityCounters
                .getOrDefault("违规调用", new LongAdder()).longValue();
            this.circuitCount += bucket.securityCounters
                .getOrDefault("限流熔断", new LongAdder()).longValue();
        }
    }
}

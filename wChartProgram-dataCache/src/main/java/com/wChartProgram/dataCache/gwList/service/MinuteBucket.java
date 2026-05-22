package com.wChartProgram.dataCache.gwList.service;

import com.wChartProgram.dataCache.gwList.dto.GatewayLog;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

public class MinuteBucket {
    private final String minuteKey;
    private final Instant timestamp;
    
    final LongAdder totalCalls = new LongAdder();
    final LongAdder successCalls = new LongAdder();
    final LongAdder failCalls = new LongAdder();
    
    final LongAdder totalTokens = new LongAdder();
    final LongAdder promptTokens = new LongAdder();
    final LongAdder completionTokens = new LongAdder();
    final LongAdder cacheHitTokens = new LongAdder();
    final LongAdder cacheMissTokens = new LongAdder();
    
    final Map<String, AppStats> appStats = new ConcurrentHashMap<>();
    final Map<String, ClientStats> clientStats = new ConcurrentHashMap<>();
    final Map<String, LongAdder> securityCounters = new ConcurrentHashMap<>();
    
    public MinuteBucket(String minuteKey, Instant timestamp) {
        this.minuteKey = minuteKey;
        this.timestamp = timestamp;
    }
    
    public String getMinuteKey() { return minuteKey; }
    public Instant getTimestamp() { return timestamp; }
    
    void updateAppStats(GatewayLog log) {
        appStats.computeIfAbsent(log.appId(), k -> new AppStats()).addCall(log);
    }
    
    void updateClientStats(GatewayLog log) {
        clientStats.computeIfAbsent(log.clientId(), k -> new ClientStats()).addCall(log);
    }
    
    static class AppStats {
        private final LongAdder totalCalls = new LongAdder();
        private final LongAdder totalTokens = new LongAdder();
        private final LongAdder promptTokens = new LongAdder();
        private final LongAdder completionTokens = new LongAdder();
        private final LongAdder cacheHitTokens = new LongAdder();
        private final LongAdder cacheMissTokens = new LongAdder();
        
        void addCall(GatewayLog log) {
            totalCalls.increment();
            if (log.usageDto() != null) {
                if (log.usageDto().totalTokens() != null) totalTokens.add(log.usageDto().totalTokens());
                if (log.usageDto().promptTokens() != null) promptTokens.add(log.usageDto().promptTokens());
                if (log.usageDto().completionTokens() != null) completionTokens.add(log.usageDto().completionTokens());
                if (log.usageDto().cacheHitTokens() != null) cacheHitTokens.add(log.usageDto().cacheHitTokens());
                if (log.usageDto().cacheMissTokens() != null) cacheMissTokens.add(log.usageDto().cacheMissTokens());
            }
        }
        
        public long getTotalCalls() { return totalCalls.longValue(); }
        public long getTotalTokens() { return totalTokens.longValue(); }
        public long getPromptTokens() { return promptTokens.longValue(); }
        public long getCompletionTokens() { return completionTokens.longValue(); }
        public long getCacheHitTokens() { return cacheHitTokens.longValue(); }
        public long getCacheMissTokens() { return cacheMissTokens.longValue(); }
        public double getCacheHitRate() {
            long hit = cacheHitTokens.longValue();
            long miss = cacheMissTokens.longValue();
            return (hit + miss) > 0 ? hit * 100.0 / (hit + miss) : 0;
        }
    }
    
    static class ClientStats {
        private final LongAdder totalCalls = new LongAdder();
        private final LongAdder successCalls = new LongAdder();
        
        void addCall(GatewayLog log) {
            totalCalls.increment();
            if (Boolean.TRUE.equals(log.success())) {
                successCalls.increment();
            }
        }
        
        public long getTotal() { return totalCalls.longValue(); }
        public long getSuccess() { return successCalls.longValue(); }
        public long getFail() { return totalCalls.longValue() - successCalls.longValue(); }
        public double getSuccessRate() {
            long total = totalCalls.longValue();
            return total > 0 ? successCalls.longValue() * 100.0 / total : 0;
        }
    }
}
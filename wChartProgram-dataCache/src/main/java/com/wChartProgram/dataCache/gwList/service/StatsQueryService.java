package com.wChartProgram.dataCache.gwList.service;

import com.wChartProgram.dataCache.gwList.dao.GatewayLogMapper;
import com.wChartProgram.dataCache.gwList.dao.MinuteAggMapper;
import com.wChartProgram.dataCache.gwList.dto.CallAnalysisDto;
import com.wChartProgram.dataCache.gwList.dto.SecurityStatsDto;
import com.wChartProgram.dataCache.gwList.dto.TokenStatsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsQueryService {

    private final RealtimeStatsAggregatorService aggregator;
    private final GatewayLogMapper gatewayLogMapper;
    private final MinuteAggMapper minuteAggMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00:00");

    public CallAnalysisDto getCallAnalysis(Instant start, Instant end) {
        try {
            var memoryResult = aggregator.getCallAnalysis(start, end);

            if (memoryResult.totalCalls < 1000 && isLargeTimeRange(start, end)) {
                var dbResult = getCallAnalysisFromDB(start, end);
                return mergeCallAnalysisResults(memoryResult, dbResult);
            }

            return convertToCallAnalysisVO(memoryResult);
        } catch (Exception e) {
            log.error("从内存读取调用分析失败，降级到数据库查询", e);
            return getCallAnalysisFromDB(start, end);
        }
    }

    private CallAnalysisDto getCallAnalysisFromDB(Instant start, Instant end) {
        String startDate = DATE_FORMATTER.format(start.atZone(java.time.ZoneOffset.UTC));
        String endDate = DATE_FORMATTER.format(end.atZone(java.time.ZoneOffset.UTC));

        Map<String, Object> total = gatewayLogMapper.getCallAnalysisTotal(startDate, endDate);
        List<Map<String, Object>> clientStats = gatewayLogMapper.getCallAnalysisByClient(startDate, endDate, 100);

        Map<String, CallAnalysisDto.ClientStats> clientStatsMap = clientStats.stream()
                .collect(Collectors.toMap(
                        m -> String.valueOf(m.get("client_id")),
                        m -> new CallAnalysisDto.ClientStats(
                                ((Number) m.get("total_calls")).longValue(),
                                ((Number) m.get("success_calls")).longValue(),
                                ((Number) m.get("fail_calls")).longValue(),
                                ((Number) m.get("success_rate")).doubleValue()
                        )
                ));

        return new CallAnalysisDto(
                ((Number) total.get("total_calls")).longValue(),
                ((Number) total.get("success_calls")).longValue(),
                ((Number) total.get("fail_calls")).longValue(),
                ((Number) total.get("success_rate")).doubleValue(),
                clientStatsMap
        );
    }

    public TokenStatsDto getTokenStats(Instant start, Instant end) {
        try {
            var memoryResult = aggregator.getTokenStats(start, end);
            return convertToTokenStatsVO(memoryResult);
        } catch (Exception e) {
            log.error("从内存读取Token统计失败，降级到数据库查询", e);
            return getTokenStatsFromDB(start, end);
        }
    }

    private TokenStatsDto getTokenStatsFromDB(Instant start, Instant end) {
        String startDate = DATE_FORMATTER.format(start.atZone(java.time.ZoneOffset.UTC));
        String endDate = DATE_FORMATTER.format(end.atZone(java.time.ZoneOffset.UTC));

        Map<String, Object> total = gatewayLogMapper.getTokenStatsTotal(startDate, endDate);
        List<Map<String, Object>> appStats = gatewayLogMapper.getTokenStatsByApp(startDate, endDate, 50);

        Map<String, TokenStatsDto.AppTokenStats> appStatsMap = appStats.stream()
                .collect(Collectors.toMap(
                        m -> String.valueOf(m.get("app_id")),
                        m -> new TokenStatsDto.AppTokenStats(
                                ((Number) m.get("total_tokens_m")).doubleValue(),
                                ((Number) m.get("prompt_tokens_m")).doubleValue(),
                                ((Number) m.get("completion_tokens_m")).doubleValue(),
                                ((Number) m.get("cache_hit_rate")).doubleValue()
                        )
                ));

        return new TokenStatsDto(
                ((Number) total.get("total_tokens_m")).doubleValue(),
                ((Number) total.get("prompt_tokens_m")).doubleValue(),
                ((Number) total.get("completion_tokens_m")).doubleValue(),
                ((Number) total.get("cache_hit_rate")).doubleValue(),
                appStatsMap
        );
    }

    public SecurityStatsDto getSecurityStats(Instant start, Instant end) {
        try {
            var memoryResult = aggregator.getSecurityStats(start, end);
            return convertToSecurityStatsVO(memoryResult, start, end);
        } catch (Exception e) {
            log.error("从内存读取安全统计失败，降级到数据库查询", e);
            return getSecurityStatsFromDB(start, end);
        }
    }

    private SecurityStatsDto getSecurityStatsFromDB(Instant start, Instant end) {
        String startDate = DATE_FORMATTER.format(start.atZone(java.time.ZoneOffset.UTC));
        String endDate = DATE_FORMATTER.format(end.atZone(java.time.ZoneOffset.UTC));

        Map<String, Object> total = gatewayLogMapper.getSecurityStatsTotal(startDate, endDate);
        List<Map<String, Object>> hourlyStats = gatewayLogMapper.getSecurityStatsHourly(startDate, endDate);

        Map<String, SecurityStatsDto.HourlyTrend> hourlyTrends = hourlyStats.stream()
                .collect(Collectors.toMap(
                        m -> String.valueOf(m.get("hour_bucket")),
                        m -> new SecurityStatsDto.HourlyTrend(
                                String.valueOf(m.get("hour_bucket")),
                                ((Number) m.get("violation_count")).longValue(),
                                ((Number) m.get("circuit_limit_count")).longValue()
                        )
                ));

        return new SecurityStatsDto(
                ((Number) total.get("apikey_pass_rate")).doubleValue(),
                ((Number) total.get("total_requests")).longValue(),
                ((Number) total.get("success_requests")).longValue(),
                ((Number) total.get("apikey_auth_fail_count")).longValue(),
                ((Number) total.get("violation_call_count")).longValue(),
                ((Number) total.get("rate_limit_circuit_count")).longValue(),
                hourlyTrends,
                new ArrayList<>()
        );
    }

    private boolean isLargeTimeRange(Instant start, Instant end) {
        return java.time.Duration.between(start, end).toHours() > 1;
    }

    private CallAnalysisDto convertToCallAnalysisVO(RealtimeStatsAggregatorService.CallAnalysisResult result) {
        Map<String, CallAnalysisDto.ClientStats> clientStatsMap = result.clientStats.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> new CallAnalysisDto.ClientStats(
                                e.getValue().total,
                                e.getValue().success,
                                e.getValue().fail,
                                e.getValue().total > 0 ? e.getValue().success * 100.0 / e.getValue().total : 0
                        )
                ));

        return new CallAnalysisDto(
                result.totalCalls,
                result.successCalls,
                result.failCalls,
                result.successRate,
                clientStatsMap
        );
    }

    private CallAnalysisDto mergeCallAnalysisResults(
            RealtimeStatsAggregatorService.CallAnalysisResult memoryResult,
            CallAnalysisDto dbResult) {
        
        Map<String, CallAnalysisDto.ClientStats> mergedClientStats = new HashMap<>(dbResult.clientStats());
        
        memoryResult.clientStats.forEach((clientId, stats) -> {
            mergedClientStats.merge(clientId, 
                new CallAnalysisDto.ClientStats(stats.total, stats.success, stats.fail,
                    stats.total > 0 ? stats.success * 100.0 / stats.total : 0),
                (existing, newStats) -> new CallAnalysisDto.ClientStats(
                    existing.total() + newStats.total(),
                    existing.success() + newStats.success(),
                    existing.fail() + newStats.fail(),
                    (existing.total() + newStats.total()) > 0 
                        ? (existing.success() + newStats.success()) * 100.0 / (existing.total() + newStats.total()) 
                        : 0
                )
            );
        });

        return new CallAnalysisDto(
                dbResult.totalCalls() + memoryResult.totalCalls,
                dbResult.successCalls() + memoryResult.successCalls,
                dbResult.failCalls() + memoryResult.failCalls,
                (dbResult.totalCalls() + memoryResult.totalCalls) > 0 
                    ? (dbResult.successCalls() + memoryResult.successCalls) * 100.0 
                        / (dbResult.totalCalls() + memoryResult.totalCalls) 
                    : 0,
                mergedClientStats
        );
    }

    private TokenStatsDto convertToTokenStatsVO(RealtimeStatsAggregatorService.TokenStatsResult result) {
        Map<String, TokenStatsDto.AppTokenStats> appStatsMap = result.appStats.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> new TokenStatsDto.AppTokenStats(
                                e.getValue().totalTokens / 1000000.0,
                                e.getValue().promptTokens / 1000000.0,
                                e.getValue().completionTokens / 1000000.0,
                                e.getValue().cacheHitRate
                        )
                ));

        return new TokenStatsDto(
                result.totalTokens / 1000000.0,
                result.promptTokens / 1000000.0,
                result.completionTokens / 1000000.0,
                result.cacheHitRate,
                appStatsMap
        );
    }

    private SecurityStatsDto convertToSecurityStatsVO(
            RealtimeStatsAggregatorService.SecurityStatsResult result,
            Instant start, Instant end) {
        
        Map<String, SecurityStatsDto.HourlyTrend> hourlyTrends = result.hourlyTrends.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> new SecurityStatsDto.HourlyTrend(
                                e.getKey(),
                                e.getValue().violationCount,
                                e.getValue().circuitCount
                        )
                ));

        return new SecurityStatsDto(
                result.apikeyPassRate,
                result.totalRequests,
                result.successRequests,
                result.apikeyAuthFailCount,
                result.violationCallCount,
                result.rateLimitCircuitCount,
                hourlyTrends,
                aggregator.getViolationDetails(start, end, 100)
        );
    }
}

package com.wChartProgram.dataCache.gwList.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.wChartProgram.dataCache.gwList.dto.GatewayLog;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SecurityClassifier {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private Cache<String, List<SecurityRule>> ruleCache;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private static final List<SecurityRule> DEFAULT_RULES = List.of(
        new SecurityRule("apikey_auth_fail", Set.of("missing_api_key", "invalid_api_key"), Set.of("401"), null),
        new SecurityRule("违规调用", Set.of("addr_forbidden", "unsafe_output_blocked", "asset_not_found"), Set.of("403"), null),
        new SecurityRule("限流熔断", Set.of("rate_limit_exceeded", "too_many_concurrent_requests", "upstream_timeout"), Set.of("429", "504"), null)
    );
    
    @PostConstruct
    public void init() {
        ruleCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .refreshAfterWrite(1, TimeUnit.MINUTES)
            .build(key -> loadRulesFromDB());
    }
    
    private List<SecurityRule> loadRulesFromDB() {
        try {
            return jdbcTemplate.query(
                "SELECT event_category, error_codes, http_statuses, duration_threshold_ms FROM security_event_config WHERE is_active = 1",
                (rs, rowNum) -> new SecurityRule(
                    rs.getString("event_category"),
                    parseJsonSet(rs.getString("error_codes")),
                    parseJsonSet(rs.getString("http_statuses")),
                    rs.getInt("duration_threshold_ms") > 0 ? rs.getInt("duration_threshold_ms") : null
                )
            );
        } catch (Exception e) {
            log.warn("加载安全规则失败，使用默认规则", e);
            return DEFAULT_RULES;
        }
    }
    
    private Set<String> parseJsonSet(String json) {
        if (json == null || json.isEmpty() || "null".equalsIgnoreCase(json)) {
            return Set.of();
        }
        try {
            List<String> list = objectMapper.readValue(json, new TypeReference<List<String>>() {});
            return list.stream().filter(s -> s != null && !s.trim().isEmpty()).collect(Collectors.toSet());
        } catch (Exception e) {
            log.warn("解析JSON数组失败: {}", json, e);
            return Set.of();
        }
    }
    
    public String classify(GatewayLog log) {
        List<SecurityRule> rules = ruleCache.get("rules", k -> loadRulesFromDB());
        
        String errorCode = log.errorCode();
        String status = log.status() != null ? String.valueOf(log.status()) : null;
        
        for (SecurityRule rule : rules) {
            if (errorCode != null && rule.errorCodes().contains(errorCode)) {
                return rule.category();
            }
        }
        
        for (SecurityRule rule : rules) {
            if (status != null && rule.httpStatuses().contains(status)) {
                return rule.category();
            }
        }
        
        if (log.durationMs() != null && log.durationMs() > 30000) {
            return "限流熔断";
        }
        
        if (Boolean.TRUE.equals(log.success())) {
            return "正常请求";
        }
        
        return "其他错误";
    }
    
    public record SecurityRule(
        String category,
        Set<String> errorCodes,
        Set<String> httpStatuses,
        Integer durationThresholdMs
    ) {}
}

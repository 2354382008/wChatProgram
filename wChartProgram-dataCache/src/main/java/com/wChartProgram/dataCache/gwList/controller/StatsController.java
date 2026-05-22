package com.wChartProgram.dataCache.gwList.controller;

import com.wChartProgram.dataCache.gwList.dto.CallAnalysisDto;
import com.wChartProgram.dataCache.gwList.dto.GatewayLog;
import com.wChartProgram.dataCache.gwList.dto.SecurityStatsDto;
import com.wChartProgram.dataCache.gwList.dto.TokenStatsDto;
import com.wChartProgram.dataCache.gwList.service.GatewayLogService;
import com.wChartProgram.dataCache.gwList.service.StatsQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

/**
 * @author 23543
 */
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {
    
    private final GatewayLogService gatewayLogService;
    private final StatsQueryService statsQueryService;
//
//    /**
//     * 接收上游推送的日志
//     */
//    @PostMapping("/report")
//    public Resu report(@RequestBody GatewayLog log) {
//        gatewayLogService.processLog(log);
//        return Response.success();
//    }
    
    /**
     * 调用分析查询
     */
    @GetMapping("/call-analysis")
    public CallAnalysisDto getCallAnalysis(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end
    ) {
        return statsQueryService.getCallAnalysis(start, end);
    }
    
    /**
     * Token计量查询
     */
    @GetMapping("/token-stats")
    public TokenStatsDto getTokenStats(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end
    ) {
        return statsQueryService.getTokenStats(start, end);
    }
    
    /**
     * 网关安全查询
     */
    @GetMapping("/security-stats")
    public SecurityStatsDto getSecurityStats(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end
    ) {
        return statsQueryService.getSecurityStats(start, end);
    }
}
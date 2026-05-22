package com.wChartProgram.dataCache.gwList.dao;

import com.wChartProgram.dataCache.gwList.dto.GatewayLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author 23543
 */
@Mapper
public interface GatewayLogMapper {

    /**
     * 批量插入日志
     */
    int batchInsert(@Param("list") List<GatewayLog> logs);

    /**
     * 插入单条日志
     */
    int insert(GatewayLog log);

    /**
     * 调用分析 - 总体指标
     */
    Map<String, Object> getCallAnalysisTotal(@Param("startDate") String startDate, 
                                               @Param("endDate") String endDate);

    /**
     * 调用分析 - 按客户端IP分组
     */
    List<Map<String, Object>> getCallAnalysisByClient(@Param("startDate") String startDate,
                                                        @Param("endDate") String endDate,
                                                        @Param("limit") int limit);

    /**
     * 调用分析 - 按小时趋势
     */
    List<Map<String, Object>> getCallAnalysisHourly(@Param("startDate") String startDate,
                                                     @Param("endDate") String endDate);

    /**
     * Token计量 - 总体指标
     */
    Map<String, Object> getTokenStatsTotal(@Param("startDate") String startDate,
                                            @Param("endDate") String endDate);

    /**
     * Token计量 - 按应用分组
     */
    List<Map<String, Object>> getTokenStatsByApp(@Param("startDate") String startDate,
                                                  @Param("endDate") String endDate,
                                                  @Param("limit") int limit);

    /**
     * Token计量 - 按天趋势
     */
    List<Map<String, Object>> getTokenStatsDaily(@Param("startDate") String startDate,
                                                  @Param("endDate") String endDate);

    /**
     * 网关安全 - 总体指标
     */
    Map<String, Object> getSecurityStatsTotal(@Param("startDate") String startDate,
                                               @Param("endDate") String endDate);

    /**
     * 网关安全 - 按小时趋势
     */
    List<Map<String, Object>> getSecurityStatsHourly(@Param("startDate") String startDate,
                                                      @Param("endDate") String endDate);

    /**
     * APIKey鉴权通过记录明细（分页）
     */
    List<Map<String, Object>> getApiKeyPassRecords(@Param("startDate") String startDate,
                                                     @Param("endDate") String endDate,
                                                     @Param("appId") String appId,
                                                     @Param("offset") int offset,
                                                     @Param("limit") int limit);

    /**
     * APIKey鉴权通过记录总数
     */
    long countApiKeyPassRecords(@Param("startDate") String startDate,
                                 @Param("endDate") String endDate,
                                 @Param("appId") String appId);

    /**
     * 违规调用记录明细（分页）
     */
    List<Map<String, Object>> getViolationRecords(@Param("startDate") String startDate,
                                                   @Param("endDate") String endDate,
                                                   @Param("offset") int offset,
                                                   @Param("limit") int limit);

    /**
     * 违规调用记录总数
     */
    long countViolationRecords(@Param("startDate") String startDate,
                                @Param("endDate") String endDate);

    /**
     * 限流熔断记录明细（分页）
     */
    List<Map<String, Object>> getLimitCircuitRecords(@Param("startDate") String startDate,
                                                      @Param("endDate") String endDate,
                                                      @Param("offset") int offset,
                                                      @Param("limit") int limit);

    /**
     * 限流熔断记录总数
     */
    long countLimitCircuitRecords(@Param("startDate") String startDate,
                                   @Param("endDate") String endDate);

    /**
     * 最近1小时实时监控
     */
    List<Map<String, Object>> getRealtimeMonitor();

    /**
     * 应用排行TOP N
     */
    List<Map<String, Object>> getAppRanking(@Param("days") int days, @Param("limit") int limit);
}
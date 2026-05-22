package com.wChartProgram.dataCache.gwList.dao;

import com.wChartProgram.dataCache.gwList.dto.MinuteAggDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface MinuteAggMapper {

    int batchUpsert(@Param("list") List<MinuteAggDto> buckets);

    List<MinuteAggDto> selectRecentHours(@Param("hours") int hours);

    List<MinuteAggDto> selectByTimeRange(@Param("startHour") String startHour,
                                          @Param("endHour") String endHour);

    Map<String, Object> getCallAnalysisFromAgg(@Param("startHour") String startHour,
                                               @Param("endHour") String endHour);

    Map<String, Object> getTokenStatsFromAgg(@Param("startHour") String startHour,
                                              @Param("endHour") String endHour);

    Map<String, Object> getSecurityStatsFromAgg(@Param("startHour") String startHour,
                                                 @Param("endHour") String endHour);
}

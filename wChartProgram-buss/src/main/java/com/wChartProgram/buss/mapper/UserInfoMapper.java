package com.wChartProgram.buss.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wChartProgram.model.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Entity com.wChartProgram.model.entity.User
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {

}

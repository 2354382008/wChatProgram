package com.wChartProgram.buss.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wChartProgram.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Entity com.wChartProgram.model.entity.User
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}

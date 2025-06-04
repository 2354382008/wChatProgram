package com.wChartProgram.buss.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wChartProgram.model.entity.LeaderRole;

import java.util.List;

/**
*
*/
public interface LeaderRoleService extends IService<LeaderRole> {

    List<LeaderRole> queryLeaderList();

}

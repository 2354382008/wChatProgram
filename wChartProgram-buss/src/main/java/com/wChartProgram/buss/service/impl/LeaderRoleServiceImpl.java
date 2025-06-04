package com.wChartProgram.buss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wChartProgram.buss.mapper.LeaderRoleMapper;
import com.wChartProgram.buss.service.LeaderRoleService;
import com.wChartProgram.model.entity.LeaderRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
*
*/
@Service
public class LeaderRoleServiceImpl extends ServiceImpl<LeaderRoleMapper, LeaderRole>
implements LeaderRoleService {

    @Autowired
    private LeaderRoleMapper leaderRoleMapper;


    @Override
    public List<LeaderRole> queryLeaderList() {
        LambdaQueryWrapper<LeaderRole> queryWrapper = new LambdaQueryWrapper<>();
        return leaderRoleMapper.selectList(queryWrapper);
    }
}

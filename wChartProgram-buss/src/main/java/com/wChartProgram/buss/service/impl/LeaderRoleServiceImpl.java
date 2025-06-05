package com.wChartProgram.buss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wChartProgram.buss.mapper.LeaderRoleMapper;
import com.wChartProgram.buss.service.LeaderRoleService;
import com.wChartProgram.common.componet.RedisComponet;
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

    @Autowired
    private RedisComponet redisComponet;

    @Override
    public List<LeaderRole> queryLeaderList() {
        redisComponet.addRedisKey("redisKey1","测试redis存储值");
        System.out.println("测试redis获取值:"+redisComponet.getRedisValue("redisKey1"));
        redisComponet.sendMessage("redisKey2","redis发送消息");
        LambdaQueryWrapper<LeaderRole> queryWrapper = new LambdaQueryWrapper<>();
        return leaderRoleMapper.selectList(queryWrapper);
    }
}

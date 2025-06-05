package com.wChartProgram.buss.controller;

import com.wChartProgram.buss.service.LeaderRoleService;
import com.wChartProgram.model.dto.WChartProgramDto;
import com.wChartProgram.model.entity.LeaderRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/program")
public class ProgramController {

    @Autowired
    private LeaderRoleService leaderRoleService;

    @PostMapping("/add")
    public List<LeaderRole> addinfo(@RequestBody WChartProgramDto wChartProgramDto){
        return leaderRoleService.queryLeaderList();
    }
    @PostMapping("/query")
    public String query(@RequestBody WChartProgramDto wChartProgramDto){
        System.out.println("返回值类型为字符串的拦截");
        return "";
//        return leaderRoleService.queryLeaderList();
    }

}

package com.wChartProgram.buss.controller;

import com.wChartProgram.buss.service.KafkaService;
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

    @Autowired
    private KafkaService KafkaService;

    @PostMapping("/add")
    public List<LeaderRole> addinfo(@RequestBody WChartProgramDto wChartProgramDto){
        return leaderRoleService.queryLeaderList();
    }
    @PostMapping("/query")
    public List<LeaderRole> query(@RequestBody WChartProgramDto wChartProgramDto){
        return leaderRoleService.queryLeaderList();
    }

    @PostMapping("/test")
    public void notice(){
        log.info("kafkakafkakafka");
        //测试状态机
        //leaderRoleService.notice();
        //测试kafka
        KafkaService.sendMessage(null,null);
    }


}

package com.wChartProgram.buss.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SchedulerTest {

    @Scheduled(fixedRate = 5000)
    public void schedulerTest(){
        log.info("进入定时任务");
    }
}

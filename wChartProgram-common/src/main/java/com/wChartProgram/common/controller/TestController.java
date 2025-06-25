package com.wChartProgram.common.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/testModule")
public class TestController {

    @PostMapping("/testRequest")
    public void testRquest(){
        log.info("测试请求是否通过!");
    }


}

package com.wChartProgram.common.enums;

import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;

public enum HandlerCode {

    ADD_HANDLER("add","添加操作"),
    UDATE_HANDLER("update","修改操作"),
    NOTICE("notice","通知事件"),
    EXECUT("execut","执行事件");

    private String event;

    private String eventMsg;

    HandlerCode(String event, String eventMsg) {
        this.event = event;
        this.eventMsg = eventMsg;
    }

}

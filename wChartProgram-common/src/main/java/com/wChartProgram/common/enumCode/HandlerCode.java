package com.wChartProgram.common.enumCode;

public enum HandlerCode {

    ADD_HANDLER("add","添加操作"),
    UDATE_HANDLER("update","修改操作");

    private String event;

    private String eventMsg;

    HandlerCode(String event, String eventMsg) {
        this.event = event;
        this.eventMsg = eventMsg;
    }

}

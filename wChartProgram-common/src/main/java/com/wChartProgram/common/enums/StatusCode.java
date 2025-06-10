package com.wChartProgram.common.enums;

/**
 * 任务执行状态控制
 */
public enum StatusCode {
    IS_NOTICE_Y("Y","已通知"),
    IS_NOTICE_N("N","未通知"),
    EXECUTE_STS_Y("Y","已执行"),
    EXECUTE_STS_N("N","未执行"),
    EXECUTE_STS_O("N","已知晓未执行")
    ;


    private String code;

    private String msg;

    StatusCode(String code, String msg) {

        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

package com.wChartProgram.common.enums;

/**
 * 自定义返回结果码值
 */
public enum ResultCode {

    LOGIN_FAIL("400","登录失败/失效"),
    PERMISSION_FAIL("403","没有权限")
    ;

    private String code;
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    ResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}

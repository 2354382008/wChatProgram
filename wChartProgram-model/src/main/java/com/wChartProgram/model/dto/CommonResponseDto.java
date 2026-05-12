package com.wChartProgram.model.dto;

import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Objects;

public final class CommonResponseDto<T> {
    public static final String DEFAULT_CODE = "000000";
    private String code = "000000";
    private long total;
    private String message;
    private String level;
    private T data;
    private Map<Object, Object> extData;

    private CommonResponseDto() {
        this.code = DEFAULT_CODE;
        this.message = "成功";
    }

    private CommonResponseDto(String code, long total, String message, Level level, T data) {
        this.code = code;
        this.total = total;
        this.message = message;
        this.level = Objects.nonNull(level) ? level.name() : null;
        this.data = data;
    }

    public static <T> CommonResponseDto<T> successMessage(String message) {
        return new CommonResponseDto("000000", 0L, message, CommonResponseDto.Level.INFO, (Object)null);
    }

    public static <T> CommonResponseDto<T> error(String code, String message) {
        return new CommonResponseDto(code, 0L, message, CommonResponseDto.Level.ERROR, (Object)null);
    }

    public static <T> CommonResponseDto<T> error(String message) {
        return new CommonResponseDto("-1", 0L, message, CommonResponseDto.Level.ERROR, (Object)null);
    }

    public static <T> CommonResponseDto<T> warn(String code, String message) {
        return new CommonResponseDto(code, 0L, message, CommonResponseDto.Level.WARN, (Object)null);
    }

    public <T> CommonResponseDto<T> data(T data) {
        return new CommonResponseDto(this.code, this.total, this.message, StringUtils.hasText(this.level) ? CommonResponseDto.Level.valueOf(this.level) : null, data);
    }

    public static <T> CommonResponseDto<T> create() {
        return new CommonResponseDto();
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getTotal() {
        return this.total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getLevel() {
        return this.level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Map<Object, Object> getExtData() {
        return this.extData;
    }

    public void setExtData(Map<Object, Object> extData) {
        this.extData = extData;
    }

    public String toString() {
        return "CommonResponseDto [code=" + this.code + ", total=" + this.total + ", message=" + this.message + ", level=" + this.level + ", data=" + this.data + ", extData=" + this.extData + "]";
    }

    public static enum Level {
        INFO,
        DEBUG,
        WARN,
        ERROR;

        private Level() {
        }
    }
}

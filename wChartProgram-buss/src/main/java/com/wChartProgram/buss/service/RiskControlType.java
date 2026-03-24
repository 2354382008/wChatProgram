package com.wChartProgram.buss.service;

import lombok.Getter;

/**
 * 风控枚举类
 * @author wangmq
 */
@Getter
public enum RiskControlType {

    /**
     * 风控标识
     */
    JINGFA("京发风控"),

    FUNDER("资金方风控");


    private String description;

    RiskControlType(String description) {
        this.description = description;
    }
}

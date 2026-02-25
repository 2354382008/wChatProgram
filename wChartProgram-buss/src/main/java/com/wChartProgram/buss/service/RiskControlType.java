package com.wChartProgram.buss.service;

/**
 * 风控枚举类
 */
public enum RiskControlType {
    JINGFA("京发风控"),
    FUNDER("资金方风控");
    
    private String description;
    RiskControlType(String description) {
        this.description = description;
    }
    public String getDescription() { return description; }
}

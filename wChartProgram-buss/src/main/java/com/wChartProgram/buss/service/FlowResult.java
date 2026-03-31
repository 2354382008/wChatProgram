package com.wChartProgram.buss.service;

import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 流程执行结果
 * 优化点：标准化的流程结果返回类
 * @author wangmq
 */
@Data
public class FlowResult {

    /**
     * 流程ID
     */
    private String flowId;

    /**
     * 申请编号
     */
    private String applyNo;

    /**
     * 是否执行成功
     */
    private boolean success;

    /**
     * 是否审批通过
     */
    private boolean approved;

    /**
     * 拒绝原因（如果未通过）
     */
    private String rejectReason;

    /**
     * 流程数据
     */
    private Map<String, Object> data;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 执行耗时（毫秒）
     */
    private long duration;

    /**
     * 错误信息（如果执行失败）
     */
    private String errorMessage;

    /**
     * 流程步骤执行详情（可选）
     */
    private Map<String, StepResult> stepResults;

    /**
     * 流程步骤执行结果
     */
    @Data
    public static class StepResult {
        /**
         * 步骤名称
         */
        private String stepName;

        /**
         * 是否执行成功
         */
        private boolean success;

        /**
         * 执行时间（毫秒）
         */
        private long duration;

        /**
         * 错误信息
         */
        private String errorMessage;

        /**
         * 步骤输出数据
         */
        private Map<String, Object> data;
    }
}

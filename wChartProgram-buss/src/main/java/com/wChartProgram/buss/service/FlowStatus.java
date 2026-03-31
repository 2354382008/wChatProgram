package com.wChartProgram.buss.service;

import lombok.Data;

import java.util.Date;

/**
 * 流程状态
 * 优化点：标准化的流程状态查询返回类
 * @author wangmq
 */
@Data
public class FlowStatus {

    /**
     * 流程ID
     */
    private String flowId;

    /**
     * 申请编号
     */
    private String applyNo;

    /**
     * 流程状态：PROCESSING-处理中, COMPLETED-已完成, FAILED-失败, TIMEOUT-超时
     */
    private String status;

    /**
     * 是否审批通过
     */
    private boolean approved;

    /**
     * 拒绝原因
     */
    private String rejectReason;

    /**
     * 当前执行的步骤
     */
    private String currentStep;

    /**
     * 进度百分比（0-100）
     */
    private int progress;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 预计剩余时间（毫秒）
     */
    private long estimatedRemainingTime;
}

package com.wChartProgram.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 工作事务表
 * @TableName work_affairs_config
 */
@Data
public class WorkAffairsConfig implements Serializable {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 事务
     */
    private String affairs;

    /**
     * 创建人（领导角色表）
     */
    private String founder;

    /**
     * 执行人（员工角色表）
     */
    private String executor;

    /**
     * 创建时间
     */
    private Date createDt;

    /**
     * 执行时完成间
     */
    private Date executDt;

    /**
     * 是否已通知(y:已通知；n:未通知)
     */
    private String isNotice;

    /**
     * 执行状态(y:已执行；n:未执行；o:已知晓未执行；p：未知晓未执行（可能短信未发送）)
     */
    private String executSts;

    /**
     * 备注
     */
    private String remark;

    private static final long serialVersionUID = 1L;
}
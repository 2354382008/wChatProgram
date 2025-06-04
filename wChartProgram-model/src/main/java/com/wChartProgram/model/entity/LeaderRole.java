package com.wChartProgram.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 领导角色表
 * @TableName leader_role
 */
@Data
public class LeaderRole implements Serializable {
    /**
     * 主键id
     */
    private Long id;

    /**
     * uuid
     */
    private String leaderUuid;

    /**
     * 名称
     */
    private String leaderName;

    /**
     * 部门
     */
    private String department;

    /**
     * 创建时间
     */
    private Date createDt;

    /**
     * 更新时间
     */
    private Date updateDt;

    /**
     * 状态(y:在职；n:离职；o:请假)
     */
    private String status;

    private static final long serialVersionUID = 1L;

}
package com.wChartProgram.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 员工角色表
 * @TableName staff_role
 */
@Data
public class StaffRole implements Serializable {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 事务
     */
    private String staffUuid;

    /**
     * 名称
     */
    private String staffName;

    /**
     * 部门
     */
    private String department;

    /**
     * 上级领导
     */
    private String leaderUuid;

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
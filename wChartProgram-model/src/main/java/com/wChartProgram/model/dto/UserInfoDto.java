package com.wChartProgram.model.dto;

import lombok.Data;

/**
 * 用户信息DTO
 */
@Data
public class UserInfoDto extends CommonRequestDto{

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String confirmPassword;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态(0:禁用;1:启用)
     */
    private Integer status;

    /**
     * 用户id
     */
    private String id;

    /**
     * 用户角色
     */
    private String userRole;

    /**
     * 用户头像
     */
    private String avatar;
}

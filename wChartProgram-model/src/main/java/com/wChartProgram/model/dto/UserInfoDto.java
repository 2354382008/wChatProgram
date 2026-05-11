package com.wChartProgram.model.dto;

import lombok.Data;

/**
 * 用户信息DTO
 */
@Data
public class UserInfoDto extends CommonRequestDto{

    private String userId;

    private String userName;

    private String userOrg;

    private String userRole;

    private String userStatus;

    private String userEmail;

    private String userPhone;

    private String userAddress;

    private String userPassword;

    private String userParentId;
}

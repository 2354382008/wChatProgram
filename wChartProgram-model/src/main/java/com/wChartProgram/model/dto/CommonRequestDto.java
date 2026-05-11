package com.wChartProgram.model.dto;

import lombok.Data;

@Data
public class CommonRequestDto extends BasePageQuery  {

    private String loginId;

    private String loginName;

    private String loginOrg;
}

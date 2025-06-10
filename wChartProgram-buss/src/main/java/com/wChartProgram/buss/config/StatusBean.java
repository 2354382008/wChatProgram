package com.wChartProgram.buss.config;

import com.wChartProgram.common.enums.StatusCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusBean {

    private String id;

    private StatusCode statusCode;
}

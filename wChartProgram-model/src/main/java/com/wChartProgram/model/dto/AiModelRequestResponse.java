package com.wChartProgram.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * AI 模型请求响应实体
 * 对应上游返回的 JSON 结构
 */
@Data
public class AiModelRequestResponse {

    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("app_id")
    private String appId;

    @JsonProperty("asset_id")
    private String assetId;

    @JsonProperty("asset_type")
    private String assetType;

    @JsonProperty("status")
    private Integer status;

    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("usage")
    private Usage usage;

    @JsonProperty("error_code")
    private String errorCode;

    @JsonProperty("duration_ms")
    private Long durationMs;
}
package com.wChartProgram.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
     * Tokens 使用情况嵌套类
     */
    @Data
    public class Usage {
        @JsonProperty("prompt_tokens")
        private Integer promptTokens;

        @JsonProperty("completion_tokens")
        private Integer completionTokens;

        @JsonProperty("total_tokens")
        private Integer totalTokens;

        @JsonProperty("cache_hit_tokens")
        private Integer cacheHitTokens;

        @JsonProperty("cache_miss_tokens")
        private Integer cacheMissTokens;

        @JsonProperty("tokens_per_second")
        private Double tokensPerSecond;

        @JsonProperty("time_to_first_token_ms")
        private Double timeToFirstTokenMs;

        @JsonProperty("time_per_output_token_ms")
        private Double timePerOutputTokenMs;
    }
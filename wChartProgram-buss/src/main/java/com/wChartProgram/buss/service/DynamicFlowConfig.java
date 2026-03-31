package com.wChartProgram.buss.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 流程配置类（优化版本）
 * 优化点：
 * 1. 支持配置流程超时时间
 * 2. 支持配置流程名称和描述
 * 3. 提供配置校验方法
 *
 * @author wangmq
 */
public class DynamicFlowConfig {

    /**
     * 流程步骤列表
     */
    private List<DynamicAsyncStep> steps = new ArrayList<>();

    /**
     * 流程名称
     * 优化点：添加流程名称，便于日志记录和监控
     */
    private String flowName;

    /**
     * 流程描述
     * 优化点：添加流程描述
     */
    private String description;

    /**
     * 流程超时时间（秒）
     * 优化点：支持配置流程超时时间
     */
    private long timeoutSeconds = 60;

    /**
     * 是否启用异步执行
     * 优化点：支持控制是否异步执行
     */
    private boolean asyncEnabled = true;

    /**
     * 添加异步步骤
     */
    public DynamicFlowConfig addStep(DynamicAsyncStep step) {
        steps.add(step);
        return this;
    }

    /**
     * 添加同步步骤
     */
    public DynamicFlowConfig addSyncStep(DynamicSyncStep step) {
        steps.add(context -> {
            step.execute(context);
            return CompletableFuture.completedFuture(null);
        });
        return this;
    }

    /**
     * 批量添加步骤
     * 优化点：支持批量添加步骤
     */
    public DynamicFlowConfig addSteps(List<DynamicAsyncStep> stepList) {
        steps.addAll(stepList);
        return this;
    }

    /**
     * 设置流程名称
     * 优化点：设置流程名称
     */
    public DynamicFlowConfig setFlowName(String flowName) {
        this.flowName = flowName;
        return this;
    }

    /**
     * 设置流程描述
     * 优化点：设置流程描述
     */
    public DynamicFlowConfig setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * 设置流程超时时间
     * 优化点：设置流程超时时间
     */
    public DynamicFlowConfig setTimeoutSeconds(long timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
        return this;
    }

    /**
     * 设置是否启用异步执行
     * 优化点：设置是否启用异步执行
     */
    public DynamicFlowConfig setAsyncEnabled(boolean asyncEnabled) {
        this.asyncEnabled = asyncEnabled;
        return this;
    }

    /**
     * 获取流程步骤
     */
    public List<DynamicAsyncStep> getSteps() {
        return steps;
    }

    /**
     * 获取流程名称
     */
    public String getFlowName() {
        return flowName;
    }

    /**
     * 获取流程描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 获取流程超时时间
     */
    public long getTimeoutSeconds() {
        return timeoutSeconds;
    }

    /**
     * 是否启用异步执行
     */
    public boolean isAsyncEnabled() {
        return asyncEnabled;
    }

    /**
     * 校验配置
     * 优化点：提供配置校验方法
     */
    public boolean validate() {
        if (steps == null || steps.isEmpty()) {
            System.err.println("流程配置错误：没有配置任何步骤");
            return false;
        }

        if (timeoutSeconds <= 0) {
            System.err.println("流程配置错误：超时时间必须大于0");
            return false;
        }

        return true;
    }

    /**
     * 获取步骤数量
     */
    public int getStepCount() {
        return steps != null ? steps.size() : 0;
    }
}

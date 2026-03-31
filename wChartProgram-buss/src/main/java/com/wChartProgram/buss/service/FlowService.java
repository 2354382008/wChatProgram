package com.wChartProgram.buss.service;

import com.wChartProgram.common.enums.NodeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 公共流程服务（提供统一的流程入口）
 * 优化点：提供标准化的流程执行接口，支持动态节点配置
 * @author wangmq
 */
@Slf4j
@Service
public class FlowService {

    @Autowired
    private FlowNodeRegistry nodeRegistry;

    @Autowired
    private RiskControlManager riskControlManager;

    /**
     * 公共流程入口（根据配置动态构建流程）
     * 优化点：提供统一的流程执行接口
     * @param flowRequest 流程请求参数
     * @return 流程执行结果
     */
    public FlowResult executeFlow(FlowRequest flowRequest) {
        // 创建流程上下文
        DynamicApprovalContext context = createContext(flowRequest);

        // 优化点：根据配置动态构建流程
        DynamicFlowConfig flowConfig = buildFlowConfig(context);

        // 执行流程
        DynamicFlowExecutor executor = new DynamicFlowExecutor();
        CompletableFuture<Void> resultFuture = executor.execute(flowConfig, context);

        // 等待流程完成
        FlowResult result = new FlowResult();
        result.setFlowId(context.getFlowId());
        result.setApplyNo(context.getApplyNo());

        try {
            // 优化点：超时时间应该可配置，建议从配置中心读取
            long timeoutSeconds = getFlowTimeout(flowRequest.getFlowType());
            resultFuture.get(timeoutSeconds, TimeUnit.SECONDS);

            // 设置执行结果
            result.setSuccess(true);
            result.setApproved(context.isApproved());
            result.setData(context.getData());

            if (!context.isApproved()) {
                result.setRejectReason(context.getRejectReason());
            }

            // 优化点：记录流程完成事件到监控系统
            logFlowCompleted(context, true);

        } catch (Exception e) {
            context.setApproved(false);
            context.setRejectReason("流程超时或异常: " + e.getMessage());

            result.setSuccess(false);
            result.setApproved(false);
            result.setRejectReason("流程超时或异常: " + e.getMessage());

            // 优化点：记录流程失败事件到监控系统
            logFlowCompleted(context, false);
        }

        return result;
    }

    /**
     * 异步执行流程（不等待结果）
     * 优化点：提供异步执行接口
     * @param flowRequest 流程请求参数
     * @return CompletableFuture
     */
    public CompletableFuture<FlowResult> executeFlowAsync(FlowRequest flowRequest) {
        return CompletableFuture.supplyAsync(() -> executeFlow(flowRequest));
    }

    /**
     * 根据流程类型执行流程
     * 优化点：支持多种流程类型
     * @param flowType 流程类型
     * @param applyNo 申请编号
     * @return 流程执行结果
     */
    public FlowResult executeFlowByType(String flowType, String applyNo) {
        FlowRequest request = new FlowRequest();
        request.setFlowType(flowType);
        request.setApplyNo(applyNo);

        // 优化点：根据流程类型加载不同的风控配置
        List<RiskControlType> riskOrder = getRiskControlOrderByFlowType(flowType);
        request.setRiskControlOrder(riskOrder);

        return executeFlow(request);
    }

    /**
     * 获取流程状态
     * 优化点：提供流程状态查询接口
     * @param flowId 流程ID
     * @return 流程状态
     */
    public FlowStatus getFlowStatus(String flowId) {
        // 优化点：实际应用中应该从数据库或分布式缓存中查询
        // FlowContextEntity entity = flowContextRepository.findByFlowId(flowId);
        // if (entity != null) {
        //     FlowStatus status = new FlowStatus();
        //     status.setFlowId(entity.getFlowId());
        //     status.setStatus(entity.getStatus());
        //     status.setApproved(entity.getApproved());
        //     status.setProgress(calculateProgress(entity));
        //     return status;
        // }

        FlowStatus status = new FlowStatus();
        status.setFlowId(flowId);
        status.setStatus("UNKNOWN");
        status.setApproved(false);
        status.setProgress(0);

        return status;
    }

    /**
     * 创建流程上下文
     * 优化点：标准化的上下文创建逻辑
     */
    private DynamicApprovalContext createContext(FlowRequest request) {
        DynamicApprovalContext context = new DynamicApprovalContext();
        context.setApplyNo(request.getApplyNo());
        context.setFlowId(request.getFlowId() != null ? request.getFlowId() : generateFlowId());

        // 设置风控顺序
        if (request.getRiskControlOrder() != null && !request.getRiskControlOrder().isEmpty()) {
            context.setRiskControlOrder(request.getRiskControlOrder());
        } else {
            // 优化点：根据流程类型设置默认风控顺序
            List<RiskControlType> defaultOrder = getRiskControlOrderByFlowType(request.getFlowType());
            context.setRiskControlOrder(defaultOrder);
        }

        // 优化点：设置初始数据
        if (request.getData() != null) {
            context.getData().putAll(request.getData());
        }

        return context;
    }

    /**
     * 动态构建流程配置
     * 优化点：根据节点配置动态构建流程
     */
    private DynamicFlowConfig buildFlowConfig(DynamicApprovalContext context) {
        DynamicFlowConfig flowConfig = new DynamicFlowConfig();

        // 获取启用的节点配置（按顺序排序）
        List<FlowNodeConfig> enabledNodes = nodeRegistry.getEnabledNodeConfigs();

        // 优化点：根据配置动态添加流程节点
        for (FlowNodeConfig nodeConfig : enabledNodes) {
            if (nodeConfig.getNodeType() == NodeType.SYNC) {
                // 同步节点
                DynamicSyncStep syncStep = nodeRegistry.getSyncNode(nodeConfig.getNodeId());
                if (syncStep != null) {
                    flowConfig.addSyncStep(syncStep);
                } else {
                    System.err.println("未找到同步节点: " + nodeConfig.getNodeId());
                    // 优化点：记录节点未找到的异常
                    logNodeNotFound(nodeConfig.getNodeId());
                }
            } else {
                // 异步节点
                DynamicAsyncStep asyncStep = nodeRegistry.getAsyncNode(nodeConfig.getNodeId());
                if (asyncStep != null) {
                    flowConfig.addStep(asyncStep);
                } else {
                    System.err.println("未找到异步节点: " + nodeConfig.getNodeId());
                    // 优化点：记录节点未找到的异常
                    logNodeNotFound(nodeConfig.getNodeId());
                }
            }
        }

        return flowConfig;
    }

    /**
     * 生成流程ID
     * 优化点：标准化的流程ID生成逻辑
     */
    private String generateFlowId() {
        // 示例：使用时间戳+随机数生成
        return "FLOW_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
    }

    /**
     * 根据流程类型获取风控顺序
     * 优化点：支持不同流程类型的风控配置
     */
    private List<RiskControlType> getRiskControlOrderByFlowType(String flowType) {
        // 优化点：实际应用中应该从配置中心读取
        // String config = configService.getProperty("flow." + flowType + ".risk.order", "");
        // return parseRiskOrder(config);

        // 默认配置
        return Arrays.asList(RiskControlType.JINGFA, RiskControlType.FUNDER);
    }

    /**
     * 获取流程超时时间
     * 优化点：支持不同流程类型的超时配置
     */
    private long getFlowTimeout(String flowType) {
        // 优化点：实际应用中应该从配置中心读取
        // return configService.getLongProperty("flow." + flowType + ".timeout.seconds", 60);
        return 60; // 默认60秒
    }

    // ==================== 优化点方法（待实现）====================

    /**
     * 优化点：记录流程完成事件
     * 实际实现示例：
     */
    private void logFlowCompleted(DynamicApprovalContext context, boolean success) {
        try{

        }catch (Exception exception){
            log.error("记录流程完成事件异常");
        }
    }

    /**
     * 优化点：记录节点未找到异常
     * 实际实现示例：
     */
    private void logNodeNotFound(String nodeId) {
        // 示例：
        // logger.error("未找到流程节点 - nodeId: {}", nodeId);
        // Metrics.counter("flow.node.not.found", "node_id", nodeId).increment();
        try{

        }catch (Exception exception){
            log.error("记录节点未找到的异常!");
        }
    }

    /**
     * 优化点：计算流程进度
     * 实际实现示例：
     */
    private int calculateProgress(Object entity) {
        // 示例：
        // FlowContextEntity flowEntity = (FlowContextEntity) entity;
        // int totalSteps = getTotalSteps(flowEntity);
        // int completedSteps = getCompletedSteps(flowEntity);
        // return totalSteps > 0 ? (completedSteps * 100 / totalSteps) : 0;
        return 0;
    }
}

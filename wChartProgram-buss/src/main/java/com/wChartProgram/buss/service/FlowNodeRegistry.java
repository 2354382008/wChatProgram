package com.wChartProgram.buss.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 流程节点注册器（管理所有流程节点的注册和查找）
 * 优化点：提供统一的节点管理机制，支持动态配置
 * @author wangmq
 */
@Slf4j
@Component
public class FlowNodeRegistry {

    /**
     * 同步节点注册表
     * 优化点：使用 ConcurrentHashMap 保证线程安全
     */
    private final Map<String, DynamicSyncStep> syncNodes = new ConcurrentHashMap<>();

    /**
     * 异步节点注册表
     * 优化点：使用 ConcurrentHashMap 保证线程安全
     */
    private final Map<String, DynamicAsyncStep> asyncNodes = new ConcurrentHashMap<>();

    /**
     * 节点配置注册表
     * 优化点：支持动态节点配置
     */
    private final Map<String, FlowNodeConfig> nodeConfigs = new ConcurrentHashMap<>();

    /**
     * 初始化节点配置
     * 优化点：从配置中心加载节点配置（示例使用硬编码，实际可从Nacos/Apollo加载）
     */
    @PostConstruct
    public void init() {
        // 优化点：实际应用中应从配置中心（如 Nacos、Apollo）动态加载配置
        registerDefaultNodes();
        loadNodeConfigs();
    }

    /**
     * 注册默认节点
     * 优化点：统一管理所有流程节点的注册
     */
    private void registerDefaultNodes() {
        // 注册同步节点
        registerSyncNode("creditAccess", this::creditAccessStep);
        registerSyncNode("creditRuleValidation", this::creditRuleValidationStep);
        registerSyncNode("imageInitialization", this::imageInitializationStep);
        registerSyncNode("createCustomerCreditLimit", this::createCustomerCreditLimitStep);
        registerSyncNode("approvalResultProcessing", this::approvalResultProcessingStep);
        // 注册异步节点
        registerAsyncNode("riskControlProcess", this::riskControlProcessStep);
    }

    /**
     * 加载节点配置
     * 优化点：支持动态配置，实际应用中可从配置中心加载
     */
    private void loadNodeConfigs() {
        //todo 后续修改为查询配置表

        // 默认配置
        nodeConfigs.put("creditAccess", FlowNodeConfig.createSyncNode("creditAccess", "授信准入", 1));
        nodeConfigs.put("creditRuleValidation", FlowNodeConfig.createSyncNode("creditRuleValidation", "授信规则校验", 2));
        nodeConfigs.put("imageInitialization", FlowNodeConfig.createSyncNode("imageInitialization", "影像件初始化", 3));
        nodeConfigs.put("riskControlProcess", FlowNodeConfig.createAsyncNode("riskControlProcess", "风控审核流程", 4));
        nodeConfigs.put("createCustomerCreditLimit", FlowNodeConfig.createSyncNode("createCustomerCreditLimit", "创建客户额度", 5));
        nodeConfigs.put("approvalResultProcessing", FlowNodeConfig.createSyncNode("approvalResultProcessing", "授信结果处理", 6));
        loadNodeConfigsFromConfigCenter();
    }

    /**
     * 优化点：支持从配置中心/数据库配置表动态加载节点配置
     * 实际实现示例：
     */
    private void loadNodeConfigsFromConfigCenter() {
        nodeConfigs.put("",FlowNodeConfig.createSyncNode("imageInitialization", "影像件初始化", 3));
    }

    /**
     * 注册同步节点
     * 优化点：提供统一的节点注册接口
     */
    public void registerSyncNode(String nodeId, DynamicSyncStep step) {
        syncNodes.put(nodeId, step);
    }

    /**
     * 注册异步节点
     * 优化点：提供统一的节点注册接口
     */
    public void registerAsyncNode(String nodeId, DynamicAsyncStep step) {
        asyncNodes.put(nodeId, step);
    }

    /**
     * 注册节点配置
     * 优化点：支持动态更新节点配置
     */
    public void registerNodeConfig(FlowNodeConfig config) {
        nodeConfigs.put(config.getNodeId(), config);
    }

    /**
     * 批量注册节点配置
     * 优化点：支持批量配置更新
     */
    public void registerNodeConfigs(List<FlowNodeConfig> configs) {
        configs.forEach(this::registerNodeConfig);
    }

    /**
     * 获取启用的节点配置（按顺序排序）
     * 优化点：支持动态筛选和排序
     */
    public List<FlowNodeConfig> getEnabledNodeConfigs() {
        return nodeConfigs.values().stream()
                .filter(FlowNodeConfig::isEnabled)
                .sorted(Comparator.comparingInt(FlowNodeConfig::getOrder))
                .collect(Collectors.toList());
    }

    /**
     * 获取同步节点
     */
    public DynamicSyncStep getSyncNode(String nodeId) {
        return syncNodes.get(nodeId);
    }

    /**
     * 获取异步节点
     */
    public DynamicAsyncStep getAsyncNode(String nodeId) {
        return asyncNodes.get(nodeId);
    }

    /**
     * 检查节点是否存在
     */
    public boolean hasNode(String nodeId) {
        return syncNodes.containsKey(nodeId) || asyncNodes.containsKey(nodeId);
    }

    /**
     * 更新节点启用状态
     * 优化点：支持动态启用/禁用节点
     */
    public void updateNodeEnabled(String nodeId, boolean enabled) {
        FlowNodeConfig config = nodeConfigs.get(nodeId);
        if (config != null) {
            config.setEnabled(enabled);
        }
    }

    /**
     * 获取所有节点配置
     */
    public Map<String, FlowNodeConfig> getAllNodeConfigs() {
        return new HashMap<>(nodeConfigs);
    }

    // ==================== 默认节点实现 ====================

    /**
     * 授信准入步骤
     * 优化点：节点逻辑集中管理，便于维护
     */
    private void creditAccessStep(DynamicApprovalContext context) {
        System.out.println("执行授信准入检查... flowId: " + context.getFlowId());
        context.getData().put("accessResult", "passed");
        System.out.println("授信准入通过");
    }

    /**
     * 授信规则校验步骤
     * 优化点：节点逻辑集中管理，便于维护
     */
    private void creditRuleValidationStep(DynamicApprovalContext context) {
        System.out.println("执行授信规则校验... flowId: " + context.getFlowId());
        context.getData().put("ruleCheckResult", "passed");
        System.out.println("授信规则校验通过");
    }

    /**
     * 影像件初始化步骤
     * 优化点：节点逻辑集中管理，便于维护
     */
    private void imageInitializationStep(DynamicApprovalContext context) {
        System.out.println("执行授信影像件初始化... flowId: " + context.getFlowId());
        context.getData().put("imageInitialized", true);
        System.out.println("授信影像件初始化完成");
    }

    /**
     * 风控审核流程步骤
     * 优化点：核心异步步骤，支持超时控制
     */
    private CompletableFuture<Void> riskControlProcessStep(DynamicApprovalContext context) {
        // 如果没有风控步骤，直接完成
        if (context.getRiskControlOrder() == null || context.getRiskControlOrder().isEmpty()) {
            System.out.println("无风控步骤，直接通过");
            return CompletableFuture.completedFuture(null);
        }
        log.info("开始风控审核流程,风控模型为：{}，流程flowId: {}" ,context.getRiskControlOrder().stream()
                .map(RiskControlType::getDescription)
                .reduce((a, b) -> a + " -> " + b)
                .orElse("无风控"),context.getFlowId());
        // 启动风控流程并等待完成
        return RiskControlManager.getInstance().startRiskProcess(context);
    }

    /**
     * 创建客户额度步骤
     * 优化点：节点逻辑集中管理，便于维护
     */
    private void createCustomerCreditLimitStep(DynamicApprovalContext context) {
        System.out.println("执行创建客户额度... flowId: " + context.getFlowId());
        // 示例额度
        context.getData().put("customerCreditLimit", 50000);
        System.out.println("客户额度创建完成，额度: " + context.getData().get("customerCreditLimit"));
    }

    /**
     * 授信结果处理步骤
     * 优化点：节点逻辑集中管理，便于维护
     */
    private void approvalResultProcessingStep(DynamicApprovalContext context) {
        System.out.println("执行授信结果处理... flowId: " + context.getFlowId());
        if (context.isApproved()) {
            context.getData().put("creditLimit", 100000);
            System.out.println("授信通过，额度: " + context.getData().get("creditLimit"));
        } else {
            System.out.println("授信被拒绝，原因: " + context.getRejectReason());
        }
    }
}

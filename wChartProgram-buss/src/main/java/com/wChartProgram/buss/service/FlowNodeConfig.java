package com.wChartProgram.buss.service;

import com.wChartProgram.common.enums.NodeType;
import lombok.Data;

/**
 * 流程节点配置类（支持动态配置）
 * @author wangmq
 */
@Data
public class FlowNodeConfig {

    /**
     * 节点唯一标识
     */
    private String nodeId;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 节点类型：sync-同步, async-异步
     */
    private NodeType nodeType;

    /**
     * 执行顺序（越小越先执行）
     */
    private int order;

    /**
     * 是否启用
     */
    private boolean enabled = true;

    /**
     * 节点超时时间（秒）
     */
    private int timeoutSeconds = 60;

    /**
     * 节点描述
     */
    private String description;


    /**
     * 创建同步节点配置
     * 优化点：提供便捷的节点配置构建方法
     */
    public static FlowNodeConfig createSyncNode(String nodeId, String nodeName, int order) {
        FlowNodeConfig config = new FlowNodeConfig();
        config.setNodeId(nodeId);
        config.setNodeName(nodeName);
        config.setNodeType(NodeType.SYNC);
        config.setOrder(order);
        return config;
    }

    /**
     * 创建异步节点配置
     * 优化点：提供便捷的节点配置构建方法
     */
    public static FlowNodeConfig createAsyncNode(String nodeId, String nodeName, int order) {
        FlowNodeConfig config = new FlowNodeConfig();
        config.setNodeId(nodeId);
        config.setNodeName(nodeName);
        config.setNodeType(NodeType.ASYNC);
        config.setOrder(order);
        return config;
    }
}

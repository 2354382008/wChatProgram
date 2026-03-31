package com.wChartProgram.common.enums;

import lombok.Getter;

/**
 * 节点执行类型枚举
 * @author wangmq
 */
@Getter
public enum NodeType {
        /**
         * 节点类型枚举
         */
        SYNC("同步节点"),
        ASYNC("异步节点");

        private final String description;

        NodeType(String description) {
            this.description = description;
        }

}
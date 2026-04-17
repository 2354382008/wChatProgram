package com.wChartProgram.model.node;

import lombok.Data;

/**
 * 双向链表
 * @author wanmgq
 */
@Data
public class DoubleListNode {


    private int val;

    /**
     * 前驱指针
     */
    private DoubleListNode prev;

    /**
     * 后继指针
     */
    private DoubleListNode next;

    public DoubleListNode(int val) {
        this.val = val;
    }
}

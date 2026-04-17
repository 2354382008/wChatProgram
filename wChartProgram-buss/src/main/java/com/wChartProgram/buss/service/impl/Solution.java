package com.wChartProgram.buss.service.impl;

/**
 * 单向链表反转
 */

 public class Solution {

    public static void main(String[] args) {
        ListNode listNode = new ListNode(0);
        Solution solution = new Solution();
        ListNode currentNode = listNode;
        for (int i = 0; i < 3; i++) {
            while (currentNode.next != null){
                currentNode = currentNode.next;
            }
            currentNode.next = new ListNode(i+1);
        }
        System.out.println("反转前：");
        for (ListNode current = listNode; current != null; current = current.next) {
            System.out.print(current.val + " ");
            if (current.next == null){
                System.out.println("");
            }
        }
        ListNode afterNode = solution.reverseList(listNode);
        System.out.println("反转后：");
        for (ListNode current = afterNode; current != null; current = current.next) {
            System.out.print(current.val + " ");
            if (current.next == null){
                System.out.println(current.next);
            }
        }
    }

     /**
      * 反转实现方法
      * @param head
      * @return
      */
    public ListNode reverseList(ListNode head) {
        // 递归终止条件：如果当前节点为空或下一个节点为空，则返回当前节点作为新的头节点
        if (head == null || head.next == null) {
            return head;
        }
        // 递归反转链表的剩余部分，并得到反转后的头节点newHead
        ListNode newHead = reverseList(head.next);
        // 将当前节点的下一个节点的next指向当前节点，实现反转
        head.next.next = head;
        // 将当前节点的next设置为null，防止链表成环
        head.next = null;
        // 返回新的头节点newHead
        return newHead;
    }

     static class ListNode {
         int val;
         ListNode next;
         ListNode(int x) {
             val = x;
         }
     }
}

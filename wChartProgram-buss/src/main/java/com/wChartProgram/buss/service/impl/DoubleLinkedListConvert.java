package com.wChartProgram.buss.service.impl;

import java.util.Arrays;

/**
 * 双向链表
 * @param <E>
 */
class ListNode<E> {

    E item;

    /**
     * 前驱指针
     */
    ListNode<E> prev;

    /**
     * 后继指针
     */
    ListNode<E> next;

    public ListNode(ListNode<E> prev, E item, ListNode<E> next) {
        this.prev = prev;
        this.item = item;
        this.next = next;
    }
}

public class DoubleLinkedListConvert {

    // 1. 双向链表 转 数组
    public static <E> Object[] linkedListToArray(ListNode<E> head) {
        int length = 0;
        ListNode<E> temp = head;
        while (temp != null) {
            length++;
            temp = temp.next;
        }

        Object[] array = new Object[length];
        temp = head;
        int index = 0;
        while (temp != null) {
            array[index++] = temp.item;
            temp = temp.next;
        }
        return array;
    }

    // 2. 数组 转 双向链表
    public static <E> ListNode<E> arrayToLinkedList(E[] array) {
        if (array == null || array.length == 0) {
            return null;
        }

        ListNode<E> head = new ListNode<>(null, array[0], null);
        ListNode<E> current = head;

        for (int i = 1; i < array.length; i++) {
            ListNode<E> newNode = new ListNode<>(current, array[i], null);
            current.next = newNode;
            current = newNode;
        }
        return head;
    }

    // 3. 双向链表 反转
    public static <E> ListNode<E> reverseDoubleLinkedList(ListNode<E> head) {
        ListNode<E> temp = null;
        ListNode<E> curr = head;

        while (curr != null) {
            temp = curr.prev;
            curr.prev = curr.next;
            curr.next = temp;
            curr = curr.prev;
        }

        if (temp != null) {
            head = temp.prev;
        }
        return head;
    }

    // 测试运行
    public static void main(String[] args) {
        Integer[] arr = {10,20,30,40,50};
        ListNode<Integer> head = arrayToLinkedList(arr);

        System.out.println("原链表："+ Arrays.toString(linkedListToArray(head)));
        ListNode<Integer> reversedHead = reverseDoubleLinkedList(head);
        System.out.println("反转后："+ Arrays.toString(linkedListToArray(reversedHead)));
    }
}
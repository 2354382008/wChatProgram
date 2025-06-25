package com.wChartProgram.buss.service.impl;

import java.util.Arrays;
import java.util.Collections;

/**
 * 贪心算法
 * 找零钱场景，求找取目标金额需要的最少纸币数
 * 目标金额用户手动输入
 * 纸币范围（1，5，10，20，50，100）
 */
public class Greedy {

    public static void main(String[] args) {
        test(3);
    }
    public static int test(int tagAmt){
        Integer[] amtList = {1,5,10,20,50,100};
        //sort 默认从小到大,reverseOrder：从大到小
        Arrays.sort(amtList, Collections.reverseOrder());
        for (int i = 0; i < amtList.length; i++) {

        }
        return 0;
    }
}

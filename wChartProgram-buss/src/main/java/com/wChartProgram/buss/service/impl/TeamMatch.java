package com.wChartProgram.buss.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class TeamMatch {
    public static void main(String[] args) {
//        teamMatch();
//        listSort1();
        listSort2();
//        listSort3();
    }

    /**
     * list集合从小到大排序(底层排序)
     */
    private static void listSort2() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("请输入整数列表（以逗号分隔）：");
            String sysStr = scanner.next();
            List<Integer> list = Arrays.asList(sysStr.split(",")).stream().map(Integer::parseInt).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(list) || list.size() <= 1) {
                return;
            }
            int n = list.size();
            for (int i = 0; i < n - 1; i++) {
                boolean swapped = false;
                for (int j = 0; j < n - 1 - i; j++) {
                    int jValue = list.get(j);
                    int jand1Value = list.get(j+1);
                    if (jValue > jand1Value) {
                        int temp = jValue;
                        list.set(j, jand1Value);
                        list.set(j+1, temp);
                        swapped = true;
                    }
                }
                if (!swapped) {
                    break;
                }
            }
            System.out.println("排序后的列表为：" + list);
        }catch (Exception exception){
            log.error(exception.getMessage(),exception);
        }
    }

    /**
     * list集合从小到大排序(工具类)
     */
    private static void listSort1() {
        List<Integer> list = new ArrayList<>(Arrays.asList(2,5,4,9,11,56,38));
        Collections.sort(list);
        System.out.println("List排序后："+list);
    }

    /**
     * n个球队，两两比赛，每个球队都要和其他球队比赛一次，问总共需要多少场比赛
     */
    private static void teamMatch() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入球队数量：");
        int n = scanner.nextInt();
        scanner.close();
        if (n < 2){
            System.out.println("球队数量不足，无法进行比赛");
            return;
        }
        int totalMatches = n * (n-1) / 2;
        System.out.println("总共需要进行"+totalMatches+"场比赛");
    }

    private static void maopaoSort(){
        //todo
    }
}

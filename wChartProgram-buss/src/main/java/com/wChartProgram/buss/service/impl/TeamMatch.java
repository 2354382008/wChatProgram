package com.wChartProgram.buss.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.message.MapMessage.MapFormat.JSON;

@Slf4j
public class TeamMatch {
    public static void main(String[] args) {
//        teamMatch();
//        listSort1();
//        listSort2();
//        listSort3();
//        bubbleSort();
        int numbers[] = {2, 7, 11, 15};
        int target = 13;
//        findTwoSum(numbers,target);
        count369(30);
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

    public static void bubbleSort() {
        int[] array = {23,45,65,1,0};
        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    swapped = true;
                }
            }
            //如果没有发生交换，则说明原数组已经有序，可以提前结束排序
            if (!swapped) {
                break;
            }
        }
    }

    public static int[] findTwoSum(int[] numbers, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        // {2，3，6} 8
        for (int i = 1; i < numbers.length; i++) {
            int complement = target - numbers[i];
            if (map.containsKey(complement)) {
                int[] result = new int[]{map.get(complement), i};
                // 按升序排列下标
                Arrays.sort(result);
                System.out.println("找到的两个数的下标为：" + Arrays.toString(result));
                return result;
            }
            map.put(numbers[i], i);
        }
        // 如果没有找到，返回空数组
        return new int[0];
    }

    /**
     * 统计[1-n] 区间
     * @param n
     * @return
     */
    public static int count369(int n) {
        int count = 0;
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            int num = i;
            while (num > 0) {
                int digit = num % 10;
                if (digit == 3 || digit == 6 || digit == 9) {
                    count++;
                }
                num /= 10;
            }
            list.add(i);
        }
        System.out.println(count);
        return count;
    }
}

package com.wChartProgram.buss.service.impl;

import com.wChartProgram.model.dto.CarLoanOrder;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JLTest {

    public void lx(){
        List<CarLoanOrder> orderList = Arrays.asList(
                new CarLoanOrder("宝马", new BigDecimal("200000")),
                new CarLoanOrder("奔驰", new BigDecimal("300000")),
                new CarLoanOrder("宝马", new BigDecimal("150000"))
        );

        /**
         * 分组+求和
         */
        Map<String, BigDecimal> brandAmountMap = orderList.stream()
                .collect(Collectors.groupingBy(CarLoanOrder::getCarName,
                        Collectors.reducing(BigDecimal.ZERO, CarLoanOrder::getCarPrice, BigDecimal::add)));
        System.out.println(brandAmountMap);
    }
}

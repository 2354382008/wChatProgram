package com.wChartProgram.model.dto;

import lombok.Data;
import org.apache.kafka.common.protocol.types.Field;

import java.math.BigDecimal;

@Data
public class CarLoanOrder {

    private String carName;

    private BigDecimal carPrice;

    public CarLoanOrder(String carName, BigDecimal carPrice) {
        this.carName = carName;
        this.carPrice = carPrice;
    }
}

package com.javarunner.saga.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {

    private Integer userId;
    private Integer productId;
    private Double amount;
    private Integer orderId;

}

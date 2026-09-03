package com.codecode.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderPaymentDTO {
    private String orderId;
    private double amount;
}

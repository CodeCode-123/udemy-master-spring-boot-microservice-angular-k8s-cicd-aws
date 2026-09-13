package com.codecode.core.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentProcessedEvent {
    private Integer paymentId;
    private Integer orderId;
    private Double totalPrice;

    @Override
    public String toString() {
        return "PaymentProcessedEvent{" +
                "paymentId=" + paymentId +
                ", orderId=" + orderId +
                ", totalPrice=" + totalPrice +
                '}';
    }
}

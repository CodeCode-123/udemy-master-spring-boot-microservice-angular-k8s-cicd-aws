package com.codecode.core.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentFailedEvent {
    private Integer orderId;
    private Double totalPrice;

    @Override
    public String toString() {
        return "PaymentFailedEvent{" +
                "orderId=" + orderId +
                ", totalPrice=" + totalPrice +
                '}';
    }
}

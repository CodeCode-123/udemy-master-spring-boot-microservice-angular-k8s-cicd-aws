package com.codecode.core.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRejectedEvent {
    private Integer orderId;

    @Override
    public String toString() {
        return "OrderRejectedEvent{" +
                "orderId=" + orderId +
                '}';
    }
}

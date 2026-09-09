package com.codecode.core.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RejectOrderCommand {
    private Integer orderId;

    @Override
    public String toString() {
        return "RejectOrderCommand{" +
                "orderId=" + orderId +
                '}';
    }
}

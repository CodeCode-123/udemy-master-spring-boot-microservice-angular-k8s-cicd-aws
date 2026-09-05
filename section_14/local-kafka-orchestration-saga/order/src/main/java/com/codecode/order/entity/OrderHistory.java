package com.codecode.order.entity;

import com.codecode.core.types.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document("ordersaga_history")
public class OrderHistory {
    @Indexed
    private Integer orderHistoryId;
    @Indexed
    private Integer orderId;
    private OrderStatus orderStatus;
    private Timestamp timestamp;
}

package com.codecode.order.exception;

public class PaymentKafkaException extends RuntimeException {
    public PaymentKafkaException(String message) {
        super(message);
    }

    public PaymentKafkaException(Throwable cause) {
        super(cause);
    }
}

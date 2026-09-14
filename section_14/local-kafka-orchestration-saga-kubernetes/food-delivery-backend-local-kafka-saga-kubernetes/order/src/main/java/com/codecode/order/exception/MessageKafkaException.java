package com.codecode.order.exception;

public class MessageKafkaException extends RuntimeException {
    public MessageKafkaException(String message) {
        super(message);
    }

    public MessageKafkaException(Throwable cause) {
        super(cause);
    }
}

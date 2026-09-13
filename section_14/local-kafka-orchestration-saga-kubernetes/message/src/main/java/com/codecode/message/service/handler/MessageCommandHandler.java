package com.codecode.message.service.handler;

import com.codecode.core.dto.command.OrderApprovalMessageCommand;
import com.codecode.core.dto.command.OrderRejectionMessageCommand;
import com.codecode.message.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MessageCommandHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageCommandHandler.class);

    private final MessageService messageService;

    @Value("${app.kafka.message-command-topic}")
    private String messageCommandTopic;

    public MessageCommandHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    @KafkaListener(topics = "${app.kafka.message-command-topic}", groupId = "message-ms-1")
    public void handleCommand(OrderApprovalMessageCommand command) {
        LOGGER.info("OrderApprovalMessageCommand: {}", command);
        messageService.getMessageApprovalEvent(command);
    }

    @KafkaListener(topics = "${app.kafka.message-command-topic}", groupId = "message-ms-2")
    public void handleCommand(OrderRejectionMessageCommand command) {
        LOGGER.info("OrderRejectionMessageCommand: {}", command);
        messageService.getMessageRejectionEvent(command);
    }


}

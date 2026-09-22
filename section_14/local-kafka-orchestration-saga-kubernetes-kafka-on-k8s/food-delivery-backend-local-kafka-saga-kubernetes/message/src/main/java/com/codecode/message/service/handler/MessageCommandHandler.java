package com.codecode.message.service.handler;

import com.codecode.message.dto.command.OrderApprovalMessageCommand;
import com.codecode.message.dto.command.OrderRejectionMessageCommand;
import com.codecode.message.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class MessageCommandHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageCommandHandler.class);

    private final MessageService messageService;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.message-command-topic}")
    private String messageCommandTopic;

    public MessageCommandHandler(MessageService messageService, ObjectMapper objectMapper) {
        this.messageService = messageService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${app.kafka.message-command-topic}", groupId = "message-ms-1")
    public void handleOrderApprovalMessageCommand(@Payload String commandStr, @Header(name="message-command", required = false) String messageCommand/*OrderApprovalMessageCommand command*/) {
        LOGGER.info("Message Command: {}", commandStr);
        if ("order-approval-message-command".equals(messageCommand)) {
            OrderApprovalMessageCommand command = objectMapper.readValue(commandStr, OrderApprovalMessageCommand.class);
            LOGGER.info("OrderApprovalMessageCommand: {}", command);
            messageService.getMessageApprovalEvent(command);
        } else if ("order-rejection-message-command".equals(messageCommand)) {
            OrderRejectionMessageCommand command = objectMapper.readValue(commandStr, OrderRejectionMessageCommand.class);
            LOGGER.info("OrderRejectionMessageCommand: {}", command);
            messageService.getMessageRejectionEvent(command);
        }
    }

//    @KafkaListener(topics = "${app.kafka.message-command-topic}", groupId = "message-ms-2")
//    public void handleOrderRejectionMessageCommand(String commandStr/*OrderRejectionMessageCommand command*/) {
//        OrderRejectionMessageCommand command = objectMapper.readValue(commandStr, OrderRejectionMessageCommand.class);
//        LOGGER.info("OrderRejectionMessageCommand: {}", command);
//        messageService.getMessageRejectionEvent(command);
//    }
}

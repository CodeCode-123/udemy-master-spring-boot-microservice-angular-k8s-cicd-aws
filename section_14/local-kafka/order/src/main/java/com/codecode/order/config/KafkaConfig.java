package com.codecode.order.config;

import com.codecode.order.dto.OrderDTO;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.aggregation.SelectionOperators;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import tools.jackson.databind.deser.jdk.StringDeserializer;
import tools.jackson.databind.ser.jdk.StringSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${app.kafka.reply-topic}")
    private String replyTopic;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.key-serializer}")
    private String keySerializer;

    @Value("${spring.kafka.producer.value-serializer}")
    private String valueSerializer;

    @Value("${spring.kafka.producer.acks}")
    private String acks;

    @Value("${spring.kafka.producer.properties.delivery.timeout.ms}")
    private String deliveryTimeout;

    @Value("${spring.kafka.producer.properties.linger.ms}")
    private String linger;

    @Value("${spring.kafka.producer.properties.request.timeout.ms}")
    private String requestTimeout;

    @Value("${spring.kafka.producer.properties.enable.idempotence}")
    private boolean idempotence;

    @Value("${spring.kafka.producer.properties.max.in.flight.requests.per.connection}")
    private Integer inflightRequests;

    @Value("${app.kafka.fetch-orderdto-topic}")
    private String fetchOrderDTOTopic;

    @Value("${spring.kafka.producer.transaction-id-prefix}")
    private String transactionalIdPrefix;

    @Value("${app.kafka.fetch-orderpaymentdto-topic}")
    private String fetchOrderPaymentDTOTopic;

    //Kafka request-reply pattern
    @Bean
    public ConcurrentMessageListenerContainer<String, String> repliesContainer(
            ConsumerFactory<String, String> consumerFactory) {
        ContainerProperties containerProperties = new ContainerProperties(replyTopic);
        return new ConcurrentMessageListenerContainer<>(consumerFactory, containerProperties);
    }

    @Bean
    public ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate(
            ProducerFactory<String, String> producerFactory,
            ConcurrentMessageListenerContainer<String, String> repliesContainer) {
        return new ReplyingKafkaTemplate<>(producerFactory, repliesContainer);
    }

    Map<String, Object> producerConfigs() {
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        config.put(ProducerConfig.ACKS_CONFIG, acks);
        config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeout);
        config.put(ProducerConfig.LINGER_MS_CONFIG, linger);
        config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeout);
        //default set is true, but explicitly set it will avoid disable it due to conflict configurations
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, idempotence);
        config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, inflightRequests);
        //define transactional id prefix
        config.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, transactionalIdPrefix);

        return config;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<String, String>(producerFactory());
    }

    @Bean("kafkaTransactionManager")
    public KafkaTransactionManager<String, String> kafkaTransactionManager(ProducerFactory<String, String> producerFactory) {
        return new KafkaTransactionManager<>(producerFactory());
    }

    @Bean
    public NewTopic createFetchOrderDTOTopic() {
        return TopicBuilder
                .name(fetchOrderDTOTopic)
                .partitions(3)
                .replicas(3)
                .build();
    }

    @Bean
    public NewTopic createFetchOrderPaymentDTOTopic() {
        return TopicBuilder
                .name(fetchOrderPaymentDTOTopic)
                .partitions(3)
                .replicas(3)
                .build();
    }
}

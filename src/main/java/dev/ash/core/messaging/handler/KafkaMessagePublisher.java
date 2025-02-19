package dev.ash.core.messaging.handler;

import dev.ash.core.messaging.core.KafkaProducerTemplate;
import dev.ash.core.messaging.core.MessageConverter;
import dev.ash.core.messaging.deduplication.DeduplicationProperties;
import dev.ash.core.messaging.deduplication.MessageDeduplicationService;
import dev.ash.core.messaging.eh.exception.MessagePublishException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of message publisher for sending messages to Kafka.
 */
@Slf4j
@Component
@AllArgsConstructor
public class KafkaMessagePublisher<K, V> implements KafkaProducerTemplate<K, V> {

    private final KafkaTemplate<K, V> kafkaTemplate;

    private final MessageConverter<V> messageConverter;

    private final MessageDeduplicationService deduplicationService;

    private final DeduplicationProperties deduplicationProperties;

    @Override
    public void send(String topic, V message) {
        send(topic, null, message);
    }

    @Override
    public void send(String topic, K key, V message) {
        send(topic, null, key, message, null);
    }

    @Override
    public void send(String topic, Integer partition, K key, V message, Headers headers) {
        try {
            if (deduplicationProperties.isEnabled() && !deduplicationService.canSendMessage(topic, key)) {
                log.debug("Skipping duplicate message with key: {} for topic: {}", key, topic);
                return;
            }
            Message<?> kafkaMessage = messageConverter.toMessage(message,
                                                                 new MessageHeaders(Map.of("topic", topic)));
            ProducerRecord<K, V> record = new ProducerRecord<>(
                topic,
                partition,
                key,
                (V) kafkaMessage.getPayload(),
                headers
            );

            kafkaTemplate.send(record).get();
            if (deduplicationProperties.isEnabled()) {
                deduplicationService.markMessageSent(topic, key);
            }
            log.debug("Successfully sent message to topic: {}", topic);
        } catch (Exception e) {
            throw new MessagePublishException("Failed to publish message to topic: " + topic, e);
        }
    }

    @Override
    public CompletableFuture<SendResult<K, V>> sendAsync(String topic, V message) {
        try {
            if (deduplicationProperties.isEnabled() && !deduplicationService.canSendMessage(topic, null)) {
                log.debug("Skipping duplicate message for topic: {}", topic);
                return CompletableFuture.completedFuture(null);
            }
            return kafkaTemplate.send(topic, message)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        throw new MessagePublishException("Failed to publish message asynchronously", throwable);
                    }
                });
        } catch (Exception e) {
            throw new MessagePublishException("Failed to initiate async message publish", e);
        }
    }
}

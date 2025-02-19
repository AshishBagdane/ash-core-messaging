package dev.ash.core.messaging.handler;

import dev.ash.core.base.eh.core.ErrorContext;
import dev.ash.core.messaging.core.KafkaMessageHandler;
import dev.ash.core.messaging.core.MessageConverter;
import dev.ash.core.messaging.eh.exception.MessageHandlingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Main message processor that coordinates message handling.
 */
@Slf4j
@Component
public class KafkaMessageProcessor {

    private final Map<String, KafkaMessageHandler<?>> handlers = new ConcurrentHashMap<>();

    private final MessageConverter<?> messageConverter;

    public KafkaMessageProcessor(MessageConverter<?> messageConverter) {
        this.messageConverter = messageConverter;
    }

    public void registerHandler(KafkaMessageHandler<?> handler, String topic) {
        handlers.put(topic, handler);
        log.info("Registered message handler for topic: {}", topic);
    }

    @SuppressWarnings("unchecked")
    public void processMessage(ConsumerRecord<?, ?> record) {
        String topic = record.topic();
        KafkaMessageHandler<?> handler = handlers.get(topic);

        if (handler == null) {
            throw new MessageHandlingException("No handler registered for topic: " + topic,
                                               ErrorContext.builder().build());
        }

        try {
            Message<?> message = convertToMessage(record);
            Object payload = messageConverter.fromMessage(message);

            if (!handler.getMessageType().isInstance(payload)) {
                throw new MessageHandlingException(
                    "Message type mismatch. Expected: " + handler.getMessageType().getSimpleName(),
                    ErrorContext.builder().build());
            }

            ((KafkaMessageHandler<Object>) handler).handleMessage(payload, message.getHeaders());
        } catch (Exception e) {
            throw new MessageHandlingException("Failed to process message for topic: " + topic, e);
        }
    }

    private Message<?> convertToMessage(ConsumerRecord<?, ?> record) {
        return org.springframework.messaging.support.MessageBuilder
            .withPayload(record.value())
            .setHeader("kafka_topic", record.topic())
            .setHeader("kafka_partition", record.partition())
            .setHeader("kafka_offset", record.offset())
            .setHeader("kafka_timestamp", record.timestamp())
            .build();
    }
}

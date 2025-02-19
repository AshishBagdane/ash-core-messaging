package dev.ash.core.messaging.handler;

import dev.ash.core.messaging.core.KafkaMessageHandler;
import dev.ash.core.messaging.core.MessageConverter;
import dev.ash.core.messaging.eh.exception.MessageHandlingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

/**
 * Default implementation of Kafka message handler providing base functionality.
 */
@Slf4j
@Component
public abstract class DefaultKafkaMessageHandler<T> implements KafkaMessageHandler<T> {

    private final MessageConverter<T> messageConverter;

    private final String topic;

    private final Class<T> messageType;

    protected DefaultKafkaMessageHandler(MessageConverter<T> messageConverter,
                                         String topic,
                                         Class<T> messageType) {
        this.messageConverter = messageConverter;
        this.topic = topic;
        this.messageType = messageType;
    }

    @Override
    public void handleMessage(T message, MessageHeaders headers) {
        try {
            log.debug("Processing message of type {} for topic {}", messageType.getSimpleName(), topic);
            processMessage(message, headers);
            log.debug("Successfully processed message for topic {}", topic);
        } catch (Exception e) {
            throw new MessageHandlingException("Failed to process message", e);
        }
    }

    @Override
    public boolean canHandle(String topic) {
        return this.topic.equals(topic);
    }

    @Override
    public Class<T> getMessageType() {
        return messageType;
    }

    /**
     * Template method for actual message processing logic.
     */
    protected abstract void processMessage(T message, MessageHeaders headers);
}

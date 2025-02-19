package dev.ash.core.messaging.core;

import dev.ash.core.messaging.eh.exception.MessageHandlingException;
import org.springframework.messaging.MessageHeaders;

/**
 * Generic interface for handling Kafka messages. Implementations should handle message processing logic for specific
 * message types.
 *
 * @param <T> The type of message to be handled
 */
public interface KafkaMessageHandler<T> {

    /**
     * Handles the received message and its headers.
     *
     * @param message The message payload to be processed
     * @param headers Message headers containing metadata
     * @throws MessageHandlingException if processing fails
     */
    void handleMessage(T message, MessageHeaders headers);

    /**
     * Determines if this handler can process messages from the given topic.
     *
     * @param topic The Kafka topic name
     * @return true if this handler can process messages from the topic
     */
    boolean canHandle(String topic);

    /**
     * Returns the class type of messages this handler can process.
     *
     * @return The class representing the message type
     */
    Class<T> getMessageType();
}


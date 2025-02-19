package dev.ash.core.messaging.core;

import dev.ash.core.messaging.eh.exception.MessageConversionException;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

/**
 * Interface for converting between domain objects and Kafka messages.
 *
 * @param <T> The type of domain object to convert
 */
public interface MessageConverter<T> {

    /**
     * Converts a Message to a domain object.
     *
     * @param message The message to convert
     * @return The converted domain object
     * @throws MessageConversionException if conversion fails
     */
    T fromMessage(Message<?> message);

    /**
     * Converts a domain object to a Message.
     *
     * @param payload The domain object to convert
     * @param headers Additional message headers
     * @return The converted Message
     * @throws MessageConversionException if conversion fails
     */
    Message<?> toMessage(T payload, MessageHeaders headers);
}

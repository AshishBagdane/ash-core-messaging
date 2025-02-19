package dev.ash.core.messaging.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ash.core.base.eh.core.ErrorContext;
import dev.ash.core.messaging.core.MessageConverter;
import dev.ash.core.messaging.eh.exception.MessageConversionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

/**
 * Default implementation of message converter supporting JSON conversion.
 */
@Slf4j
@Component
public class KafkaMessageConverter<T> implements MessageConverter<T> {

    private final ObjectMapper objectMapper;

    public KafkaMessageConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public T fromMessage(Message<?> message) {
        try {
            byte[] payload = (byte[]) message.getPayload();
            @SuppressWarnings("unchecked")
            Class<T> targetType = (Class<T>) message.getHeaders().get("targetType", Class.class);
            if (targetType == null) {
                throw new MessageConversionException("Target type not specified in message headers",
                                                     ErrorContext.builder().build());
            }
            return objectMapper.readValue(payload, targetType);
        } catch (Exception e) {
            throw new MessageConversionException("Failed to convert message from bytes", e);
        }
    }

    @Override
    public Message<?> toMessage(T payload, MessageHeaders headers) {
        try {
            byte[] messagePayload = objectMapper.writeValueAsBytes(payload);
            return org.springframework.messaging.support.MessageBuilder
                .withPayload(messagePayload)
                .copyHeaders(headers)
                .setHeader("targetType", payload.getClass())
                .build();
        } catch (Exception e) {
            throw new MessageConversionException("Failed to convert payload to message", e);
        }
    }
}


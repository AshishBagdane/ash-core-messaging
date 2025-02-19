package dev.ash.core.messaging.eh.exception;

import dev.ash.core.base.eh.core.ErrorCode;
import dev.ash.core.base.eh.core.ErrorContext;
import dev.ash.core.base.eh.exception.base.AbstractApplicationException;

/**
 * Exception thrown when there is a connection error with Kafka broker.
 */
public class KafkaConnectionException extends AbstractApplicationException {

    public KafkaConnectionException(ErrorContext errorContext) {
        super(ErrorCode.INTEGRATION_KAFKA_CONNECTION_ERROR, errorContext);
    }

    public KafkaConnectionException(String message, ErrorContext errorContext) {
        super(ErrorCode.INTEGRATION_KAFKA_CONNECTION_ERROR, message, errorContext);
    }

    public KafkaConnectionException(Throwable cause, ErrorContext errorContext) {
        super(ErrorCode.INTEGRATION_KAFKA_CONNECTION_ERROR, cause, errorContext);
    }
}

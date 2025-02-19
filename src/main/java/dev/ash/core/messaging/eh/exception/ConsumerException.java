package dev.ash.core.messaging.eh.exception;

import dev.ash.core.base.eh.core.ErrorCode;
import dev.ash.core.base.eh.exception.base.AbstractApplicationException;

/**
 * Exception thrown when there is an error with Kafka consumer operations.
 */
public class ConsumerException extends AbstractApplicationException {

    public ConsumerException(String operation, Throwable cause) {
        super(
            ErrorCode.INTEGRATION_KAFKA_CONSUMER_ERROR,
            cause,
            createErrorContext()
                .attribute("operation", operation)
                .attribute("errorType", cause.getClass().getSimpleName())
                .attribute("errorMessage", cause.getMessage())
                .build()
        );
    }

    public ConsumerException(String operation, String reason) {
        super(
            ErrorCode.INTEGRATION_KAFKA_CONSUMER_ERROR,
            String.format("Internal error during '%s': %s", operation, reason),
            createErrorContext()
                .attribute("operation", operation)
                .attribute("reason", reason)
                .build()
        );
    }
}

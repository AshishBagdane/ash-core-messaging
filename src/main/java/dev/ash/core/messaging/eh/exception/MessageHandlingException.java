package dev.ash.core.messaging.eh.exception;

import dev.ash.core.base.eh.core.ErrorCode;
import dev.ash.core.base.eh.core.ErrorContext;
import dev.ash.core.base.eh.exception.base.AbstractApplicationException;

/**
 * Exception thrown when there is an error processing a Kafka message.
 */
public class MessageHandlingException extends AbstractApplicationException {


    public MessageHandlingException(String message, ErrorContext errorContext) {
        super(ErrorCode.INTEGRATION_KAFKA_MESSAGE_HANDLING, message, errorContext);
    }

    public MessageHandlingException(String operation, Throwable cause) {
        super(
            ErrorCode.INTEGRATION_KAFKA_MESSAGE_HANDLING,
            cause,
            createErrorContext()
                .attribute("operation", operation)
                .attribute("errorType", cause.getClass().getSimpleName())
                .attribute("errorMessage", cause.getMessage())
                .build()
        );
    }

    public MessageHandlingException(String operation, String reason) {
        super(
            ErrorCode.INTEGRATION_KAFKA_MESSAGE_HANDLING,
            String.format("Internal error during '%s': %s", operation, reason),
            createErrorContext()
                .attribute("operation", operation)
                .attribute("reason", reason)
                .build()
        );
    }
}


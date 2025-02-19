package dev.ash.core.messaging.eh.exception;

import dev.ash.core.base.eh.core.ErrorCode;
import dev.ash.core.base.eh.exception.base.AbstractApplicationException;

/**
 * Exception thrown when there is an error publishing a message to Kafka.
 */
public class MessagePublishException extends AbstractApplicationException {

    public MessagePublishException(String operation, Throwable cause) {
        super(
            ErrorCode.INTEGRATION_KAFKA_MESSAGE_PUBLISH,
            cause,
            createErrorContext()
                .attribute("operation", operation)
                .attribute("errorType", cause.getClass().getSimpleName())
                .attribute("errorMessage", cause.getMessage())
                .build()
        );
    }

    public MessagePublishException(String operation, String reason) {
        super(
            ErrorCode.INTEGRATION_KAFKA_MESSAGE_PUBLISH,
            String.format("Internal error during '%s': %s", operation, reason),
            createErrorContext()
                .attribute("operation", operation)
                .attribute("reason", reason)
                .build()
        );
    }
}

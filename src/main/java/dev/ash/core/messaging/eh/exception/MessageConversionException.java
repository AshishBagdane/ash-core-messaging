package dev.ash.core.messaging.eh.exception;

import dev.ash.core.base.eh.core.ErrorCode;
import dev.ash.core.base.eh.core.ErrorContext;
import dev.ash.core.base.eh.exception.base.AbstractApplicationException;

/**
 * Exception thrown when there is an error converting messages to/from Kafka format.
 */
public class MessageConversionException extends AbstractApplicationException {

    public MessageConversionException(String message, ErrorContext errorContext) {
        super(ErrorCode.INTEGRATION_KAFKA_MESSAGE_CONVERSION, message, errorContext);
    }

    public MessageConversionException(String operation, Throwable cause) {
        super(
            ErrorCode.INTEGRATION_KAFKA_MESSAGE_CONVERSION,
            cause,
            createErrorContext()
                .attribute("operation", operation)
                .attribute("errorType", cause.getClass().getSimpleName())
                .attribute("errorMessage", cause.getMessage())
                .build()
        );
    }

    public MessageConversionException(String operation, String reason) {
        super(
            ErrorCode.INTEGRATION_KAFKA_MESSAGE_CONVERSION,
            String.format("Internal error during '%s': %s", operation, reason),
            createErrorContext()
                .attribute("operation", operation)
                .attribute("reason", reason)
                .build()
        );
    }

}

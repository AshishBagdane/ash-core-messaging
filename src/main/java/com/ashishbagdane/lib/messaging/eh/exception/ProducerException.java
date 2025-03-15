package com.ashishbagdane.lib.messaging.eh.exception;

import com.ashishbagdane.lib.base.eh.core.ErrorCode;
import com.ashishbagdane.lib.base.eh.exception.base.AbstractApplicationException;

/**
 * Exception thrown when there is an error with Kafka producer operations.
 */
public class ProducerException extends AbstractApplicationException {

    public ProducerException(String operation, Throwable cause) {
        super(
            ErrorCode.INTEGRATION_KAFKA_PRODUCER_ERROR,
            cause,
            createErrorContext()
                .attribute("operation", operation)
                .attribute("errorType", cause.getClass().getSimpleName())
                .attribute("errorMessage", cause.getMessage())
                .build()
        );
    }

    public ProducerException(String operation, String reason) {
        super(
            ErrorCode.INTEGRATION_KAFKA_PRODUCER_ERROR,
            String.format("Internal error during '%s': %s", operation, reason),
            createErrorContext()
                .attribute("operation", operation)
                .attribute("reason", reason)
                .build()
        );
    }
}

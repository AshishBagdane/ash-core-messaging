package com.ashishbagdane.lib.messaging.eh.exception;

import com.ashishbagdane.lib.base.eh.core.ErrorCode;
import com.ashishbagdane.lib.base.eh.core.ErrorContext;
import com.ashishbagdane.lib.base.eh.exception.base.AbstractApplicationException;

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

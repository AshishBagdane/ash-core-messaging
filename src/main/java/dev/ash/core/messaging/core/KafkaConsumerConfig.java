package dev.ash.core.messaging.core;

import java.time.Duration;

/**
 * Configuration interface for Kafka consumers. Defines consumer properties and behavior settings.
 */
public interface KafkaConsumerConfig {

    /**
     * Gets the consumer group ID.
     *
     * @return The consumer group ID
     */
    String getGroupId();

    /**
     * Gets the consumer client ID.
     *
     * @return The client ID
     */
    String getClientId();

    /**
     * Gets the consumer processing mode.
     *
     * @return The ConsumerMode (SINGLE or BATCH)
     */
    ConsumerMode getMode();

    /**
     * Gets the batch size for batch processing mode.
     *
     * @return The batch size
     */
    int getBatchSize();

    /**
     * Gets the poll timeout duration.
     *
     * @return The poll timeout
     */
    Duration getPollTimeout();
}

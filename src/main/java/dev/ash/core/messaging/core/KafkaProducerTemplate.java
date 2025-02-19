package dev.ash.core.messaging.core;

import dev.ash.core.messaging.eh.exception.MessagePublishException;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

/**
 * Template interface for producing Kafka messages. Provides methods for sending messages with different combinations of
 * keys, partitions, and headers.
 *
 * @param <K> The type of the message key
 * @param <V> The type of the message value
 */
public interface KafkaProducerTemplate<K, V> {

    /**
     * Sends a message to the specified topic.
     *
     * @param topic   The destination topic
     * @param message The message to send
     * @throws MessagePublishException if sending fails
     */
    void send(String topic, V message);

    /**
     * Sends a keyed message to the specified topic.
     *
     * @param topic   The destination topic
     * @param key     The message key
     * @param message The message to send
     * @throws MessagePublishException if sending fails
     */
    void send(String topic, K key, V message);

    /**
     * Sends a message with complete metadata.
     *
     * @param topic     The destination topic
     * @param partition Target partition (null for auto-assignment)
     * @param key       The message key
     * @param message   The message to send
     * @param headers   Additional message headers
     * @throws MessagePublishException if sending fails
     */
    void send(String topic, Integer partition, K key, V message, Headers headers);

    /**
     * Asynchronously sends a message and returns a future.
     *
     * @param topic   The destination topic
     * @param message The message to send
     * @return CompletableFuture containing the send result
     */
    CompletableFuture<SendResult<K, V>> sendAsync(String topic, V message);
}

package dev.ash.core.messaging.deduplication;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Service to handle message deduplication using an in-memory cache.
 */
@Slf4j
@Component
public class MessageDeduplicationService {

    private final Cache<String, Boolean> messageCache;

    public MessageDeduplicationService(
        @Value("${ash.kafka.deduplication.window-minutes:5}") long deduplicationWindowMinutes,
        @Value("${ash.kafka.deduplication.cache-size:10000}") long cacheSize) {

        Duration deduplicationWindow = Duration.ofMinutes(deduplicationWindowMinutes);
        this.messageCache = Caffeine.newBuilder()
            .maximumSize(cacheSize)
            .expireAfterWrite(deduplicationWindow.toMinutes(), TimeUnit.MINUTES)
            .build();
    }

    /**
     * Checks if a message with the given key can be sent.
     *
     * @param topic The Kafka topic
     * @param key   The message key
     * @return true if the message can be sent, false if it's a duplicate
     */
    public boolean canSendMessage(String topic, Object key) {
        if (key == null) {
            return true; // Allow messages without keys
        }

        String cacheKey = generateCacheKey(topic, key);
        Boolean existing = messageCache.getIfPresent(cacheKey);

        if (existing != null) {
            log.debug("Duplicate message detected for key: {} in topic: {}", key, topic);
            return false;
        }

        messageCache.put(cacheKey, true);
        return true;
    }

    /**
     * Marks a message as successfully sent.
     *
     * @param topic The Kafka topic
     * @param key   The message key
     */
    public void markMessageSent(String topic, Object key) {
        if (key != null) {
            String cacheKey = generateCacheKey(topic, key);
            messageCache.put(cacheKey, true);
            log.debug("Marked message as sent for key: {} in topic: {}", key, topic);
        }
    }

    private String generateCacheKey(String topic, Object key) {
        return topic + ":" + key.toString();
    }
}

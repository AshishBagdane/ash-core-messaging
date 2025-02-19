package dev.ash.core.messaging.deduplication;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for message deduplication.
 */
@Component
@ConfigurationProperties(prefix = "ash.kafka.deduplication")
@Data
public class DeduplicationProperties {

    /**
     * Duration in minutes for which to prevent duplicate messages
     */
    private final long windowMinutes = 5;

    /**
     * Maximum size of the deduplication cache
     */
    private final long cacheSize = 10000;

    /**
     * Whether deduplication is enabled
     */
    private final boolean enabled = true;
}

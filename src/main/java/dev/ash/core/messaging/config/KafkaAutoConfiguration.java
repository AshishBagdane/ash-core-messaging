package dev.ash.core.messaging.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

/**
 * Auto-configuration for Kafka messaging infrastructure. Enables and configures Kafka messaging when
 * 'ash.kafka.enabled' is true.
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "ash.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(KafkaProperties.class)
@Import({
    KafkaProducerConfiguration.class,
    KafkaConsumerConfiguration.class,
    KafkaHealthIndicatorConfiguration.class
})
public class KafkaAutoConfiguration {
    // Base configuration class that imports specific configurations
    // This follows separation of concerns while maintaining a single entry point
}

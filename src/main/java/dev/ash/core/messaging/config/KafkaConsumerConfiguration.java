package dev.ash.core.messaging.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for Kafka Consumer settings. Creates and configures the necessary beans for Kafka message
 * consumption.
 */
@Configuration
@ConditionalOnProperty(prefix = "ash.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
public class KafkaConsumerConfiguration {

    /**
     * Creates consumer configuration properties based on the provided Kafka properties.
     *
     * @param kafkaProperties The Kafka properties
     * @return Map of consumer configuration properties
     */
    @Bean
    public Map<String, Object> consumerConfigs(KafkaProperties kafkaProperties) {
        Map<String, Object> props = new HashMap<>();

        // Basic configuration
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getConsumer().getGroupId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // Consumer specific configurations
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaProperties.getConsumer().getAutoOffsetReset());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, kafkaProperties.getConsumer().getMaxPollRecords());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaProperties.getConsumer().isEnableAutoCommit());
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,
                  kafkaProperties.getConsumer().getAutoCommitIntervalMs());

        // Security configuration
        if (!"PLAINTEXT".equals(kafkaProperties.getSecurity().getProtocol())) {
            props.put("security.protocol", kafkaProperties.getSecurity().getProtocol());
            if (kafkaProperties.getSecurity().getUsername() != null) {
                props.put("sasl.jaas.config",
                          String.format(
                              "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";",
                              kafkaProperties.getSecurity().getUsername(),
                              kafkaProperties.getSecurity().getPassword()
                          )
                );
            }
        }

        return props;
    }

    /**
     * Creates the consumer factory bean.
     *
     * @param consumerConfigs The consumer configuration properties
     * @return ConsumerFactory for creating Kafka consumers
     */
    @Bean
    public ConsumerFactory<String, String> consumerFactory(Map<String, Object> consumerConfigs) {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs);
    }

    /**
     * Creates the Kafka listener container factory.
     *
     * @param consumerFactory The consumer factory
     * @return ConcurrentKafkaListenerContainerFactory for creating Kafka listener containers
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
        ConsumerFactory<String, String> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}

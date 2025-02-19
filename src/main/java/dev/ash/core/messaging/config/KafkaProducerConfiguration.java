package dev.ash.core.messaging.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for Kafka Producer settings. Creates and configures the necessary beans for Kafka message
 * production.
 */
@Configuration
@ConditionalOnProperty(prefix = "ash.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
public class KafkaProducerConfiguration {

    /**
     * Creates producer configuration properties based on the provided Kafka properties.
     *
     * @param kafkaProperties The Kafka properties
     * @return Map of producer configuration properties
     */
    @Bean
    public Map<String, Object> producerConfigs(KafkaProperties kafkaProperties) {
        Map<String, Object> props = new HashMap<>();

        // Basic configuration
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // Producer specific configurations
        props.put(ProducerConfig.RETRIES_CONFIG, kafkaProperties.getProducer().getRetries());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, kafkaProperties.getProducer().getBatchSize());
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, kafkaProperties.getProducer().isEnableIdempotence());
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, kafkaProperties.getProducer().getMaxBlockMs());

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

        // Transaction configuration
        if (kafkaProperties.getProducer().getTransactionIdPrefix() != null) {
            props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG,
                      kafkaProperties.getProducer().getTransactionIdPrefix() + "-" + System.currentTimeMillis());
        }

        return props;
    }

    /**
     * Creates the producer factory bean.
     *
     * @param producerConfigs The producer configuration properties
     * @return ProducerFactory for creating Kafka producers
     */
    @Bean
    public ProducerFactory<String, String> producerFactory(Map<String, Object> producerConfigs) {
        return new DefaultKafkaProducerFactory<>(producerConfigs);
    }

    /**
     * Creates the KafkaTemplate bean for sending messages.
     *
     * @param producerFactory The producer factory
     * @return KafkaTemplate for sending messages to Kafka
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}

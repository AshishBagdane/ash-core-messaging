package dev.ash.core.messaging.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Configuration class for Kafka health monitoring. Provides health check capabilities for Kafka brokers.
 */
@Configuration
@ConditionalOnClass(KafkaAdmin.class)
@ConditionalOnProperty(prefix = "ash.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
public class KafkaHealthIndicatorConfiguration {

    /**
     * Creates a Kafka admin client for health checks.
     *
     * @param kafkaProperties The Kafka properties
     * @return AdminClient for Kafka administration
     */
    @Bean
    public AdminClient kafkaAdminClient(KafkaProperties kafkaProperties) {
        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaProperties.getBootstrapServers());

        // Add security configuration if needed
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

        return AdminClient.create(props);
    }

    /**
     * Creates a health indicator for Kafka.
     *
     * @param adminClient The Kafka admin client
     * @return HealthIndicator for monitoring Kafka health
     */
    @Bean
    public HealthIndicator kafkaHealthIndicator(AdminClient adminClient) {
        return () -> {
            Health.Builder builder = new Health.Builder();

            try {
                // Try to list topics with a timeout
                ListTopicsOptions options = new ListTopicsOptions();
                options.timeoutMs(5000); // 5 seconds timeout
                adminClient.listTopics(options)
                    .names()
                    .get(5, TimeUnit.SECONDS);

                // If successful, mark as UP
                builder.up()
                    .withDetail("status", "Available")
                    .withDetail("mode", "Running");

            } catch (Exception e) {
                // If there's an error, mark as DOWN
                builder.down()
                    .withDetail("status", "Unavailable")
                    .withDetail("error", e.getMessage())
                    .withDetail("mode", "Not Connected");
            }

            return builder.build();
        };
    }
}

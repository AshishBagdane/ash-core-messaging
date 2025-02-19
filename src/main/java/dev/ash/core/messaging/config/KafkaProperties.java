package dev.ash.core.messaging.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * Configuration properties for Kafka messaging infrastructure. This class provides a centralized configuration for all
 * Kafka-related settings.
 */
@Data
@ConfigurationProperties(prefix = "ash.kafka")
public class KafkaProperties {

    /**
     * Flag to enable/disable Kafka configuration.
     */
    private boolean enabled = true;

    /**
     * Comma-separated list of host:port pairs for establishing the initial connection to the Kafka cluster.
     */
    @NotBlank(message = "Bootstrap servers must be provided")
    @Pattern(regexp = "^[\\w\\.-]+(:\\d+)?(?:,[\\w\\.-]+(:\\d+)?)*$",
        message = "Invalid bootstrap servers format. Use host:port,host:port")
    private String bootstrapServers;

    /**
     * Security configuration for Kafka connections.
     */
    @Valid
    @NestedConfigurationProperty
    private final Security security = new Security();

    /**
     * Producer-specific configurations.
     */
    @Valid
    @NestedConfigurationProperty
    private final Producer producer = new Producer();

    /**
     * Consumer-specific configurations.
     */
    @Valid
    @NestedConfigurationProperty
    private final Consumer consumer = new Consumer();

    /**
     * Security configuration properties for Kafka.
     */
    @Data
    public static class Security {

        /**
         * Security protocol to be used for Kafka connections.
         */
        @NotNull(message = "Security protocol must be specified")
        @Pattern(regexp = "^(PLAINTEXT|SSL|SASL_PLAINTEXT|SASL_SSL)$",
            message = "Security protocol must be one of: PLAINTEXT, SSL, SASL_PLAINTEXT, SASL_SSL")
        private String protocol = "PLAINTEXT";

        /**
         * Username for SASL authentication.
         */
        private String username;

        /**
         * Password for SASL authentication.
         */
        private String password;
    }

    /**
     * Producer configuration properties.
     */
    @Data
    public static class Producer {

        /**
         * Prefix to be used for transactional IDs. Required when using transactional messaging.
         */
        private String transactionIdPrefix;

        /**
         * Number of retries for producer operations.
         */
        @Min(value = 0, message = "Retries must be greater than or equal to 0")
        private int retries = 3;

        /**
         * The batch size in bytes when batching multiple records sent to the same partition.
         */
        @Min(value = 0, message = "Batch size must be greater than or equal to 0")
        private int batchSize = 16384;

        /**
         * Flag to enable idempotent producer.
         */
        private boolean enableIdempotence = false;

        /**
         * The maximum time to block before throwing a TimeoutException when sending messages.
         */
        @Min(value = 0, message = "Max block ms must be greater than or equal to 0")
        private long maxBlockMs = 60000;
    }

    /**
     * Consumer configuration properties.
     */
    @Data
    public static class Consumer {

        /**
         * Unique string that identifies the consumer group this consumer belongs to.
         */
        @NotBlank(message = "Group ID must be provided")
        private String groupId;

        /**
         * What to do when there is no initial offset in Kafka or if the current offset does not exist anymore on the
         * server.
         */
        @Pattern(regexp = "^(earliest|latest|none)$",
            message = "Auto offset reset must be one of: earliest, latest, none")
        private String autoOffsetReset = "earliest";

        /**
         * Maximum number of records returned in a single call to poll().
         */
        @Min(value = 1, message = "Max poll records must be greater than 0")
        private int maxPollRecords = 500;

        /**
         * If true, the consumer's offset will be periodically committed in the background.
         */
        private boolean enableAutoCommit = true;

        /**
         * The frequency in milliseconds that the consumer offsets are auto-committed to Kafka.
         */
        @Min(value = 0, message = "Auto commit interval must be greater than or equal to 0")
        private long autoCommitIntervalMs = 5000;
    }
}

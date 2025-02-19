package dev.ash.core.messaging.core;

/**
 * Enum defining consumer processing modes.
 */
enum ConsumerMode {
    SINGLE,    // Process messages one at a time
    BATCH      // Process messages in batches
}

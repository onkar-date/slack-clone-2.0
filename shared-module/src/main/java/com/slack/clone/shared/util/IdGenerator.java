package com.slack.clone.shared.util;

import java.util.UUID;

/**
 * Utility class for ID generation
 */
public final class IdGenerator {

    private IdGenerator() {
        // Utility class
    }

    /**
     * Generate a unique UUID string
     */
    public static String generateId() {
        return UUID.randomUUID().toString();
    }
}

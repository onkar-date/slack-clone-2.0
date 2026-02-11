package com.slack.clone.shared.exception;

/**
 * Thrown when business validation fails
 */
public class ValidationException extends SlackCloneException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

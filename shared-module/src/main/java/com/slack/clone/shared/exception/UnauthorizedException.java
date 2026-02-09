package com.slack.clone.shared.exception;

/**
 * Thrown when unauthorized access is attempted
 */
public class UnauthorizedException extends SlackCloneException {

    public UnauthorizedException(String message) {
        super(message);
    }
}

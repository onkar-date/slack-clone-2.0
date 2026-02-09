package com.slack.clone.shared.exception;

/**
 * Base exception for all Slack Clone exceptions
 */
public abstract class SlackCloneException extends RuntimeException {

    protected SlackCloneException(String message) {
        super(message);
    }

    protected SlackCloneException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.slack.clone.shared.exception;

/**
 * Thrown when a requested resource is not found
 */
public class ResourceNotFoundException extends SlackCloneException {

    public ResourceNotFoundException(String resourceType, String id) {
        super(String.format("%s not found with id: %s", resourceType, id));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}

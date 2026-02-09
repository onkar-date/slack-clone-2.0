package com.slack.clone.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for sending a message
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {

    @NotBlank(message = "Content is required")
    @Size(max = 5000, message = "Message content must not exceed 5000 characters")
    private String content;
}

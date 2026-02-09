package com.slack.clone.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Message DTO for API responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDTO {
    private String id;
    private String senderId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime editedAt;
    private String type;
}

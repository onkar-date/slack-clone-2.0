package com.slack.clone.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DM conversation DTO for API responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DmConversationDTO {
    private String id;
    private String user1Id;
    private String user2Id;
    private LocalDateTime createdAt;
}

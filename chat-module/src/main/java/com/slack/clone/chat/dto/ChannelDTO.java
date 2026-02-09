package com.slack.clone.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Channel DTO for API responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChannelDTO {
    private String id;
    private String name;
    private String description;
    private String createdBy;
    private LocalDateTime createdAt;
    private Integer memberCount;
}

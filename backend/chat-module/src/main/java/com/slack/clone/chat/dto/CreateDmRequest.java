package com.slack.clone.chat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a DM conversation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDmRequest {

    @NotBlank(message = "Peer user ID is required")
    private String peerUserId;
}

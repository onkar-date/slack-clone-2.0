package com.slack.clone.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a channel
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateChannelRequest {

    @NotBlank(message = "Channel name is required")
    @Size(min = 3, max = 100, message = "Name must be 3-100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}

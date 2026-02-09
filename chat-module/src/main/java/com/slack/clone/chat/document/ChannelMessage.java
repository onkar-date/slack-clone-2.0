package com.slack.clone.chat.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Channel message document stored in MongoDB
 */
@Document(collection = "channel_messages")
@CompoundIndex(name = "channel_created_idx", def = "{'channelId': 1, 'createdAt': 1}")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChannelMessage {

    @Id
    private String id;

    @Indexed
    private String channelId;

    @Indexed
    private String senderId;

    private String content;

    @Indexed
    private LocalDateTime createdAt;

    private LocalDateTime editedAt;

    @Builder.Default
    private String type = "TEXT";
}

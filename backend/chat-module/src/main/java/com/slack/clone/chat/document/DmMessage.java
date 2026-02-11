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
 * DM message document stored in MongoDB
 */
@Document(collection = "dm_messages")
@CompoundIndex(name = "conversation_created_idx", def = "{'conversationId': 1, 'createdAt': 1}")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DmMessage {

    @Id
    private String id;

    @Indexed
    private String conversationId;

    @Indexed
    private String senderId;

    private String content;

    @Indexed
    private LocalDateTime createdAt;

    private LocalDateTime editedAt;

    @Builder.Default
    private String type = "TEXT";
}

package com.slack.clone.chat.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * DM conversation entity stored in PostgreSQL
 */
@Entity
@Table(name = "dm_conversations", indexes = {
        @Index(name = "idx_user_pair", columnList = "user1_id,user2_id", unique = true),
        @Index(name = "idx_user1", columnList = "user1_id"),
        @Index(name = "idx_user2", columnList = "user2_id")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DmConversation {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "user1_id", nullable = false, length = 36)
    private String user1Id;

    @Column(name = "user2_id", nullable = false, length = 36)
    private String user2Id;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

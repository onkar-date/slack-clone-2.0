package com.slack.clone.chat.repository;

import com.slack.clone.chat.entity.DmConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for DmConversation entity
 */
@Repository
public interface DmConversationRepository extends JpaRepository<DmConversation, String> {

    @Query("SELECT d FROM DmConversation d WHERE " +
            "(d.user1Id = :user1Id AND d.user2Id = :user2Id) OR " +
            "(d.user1Id = :user2Id AND d.user2Id = :user1Id)")
    Optional<DmConversation> findByUserPair(String user1Id, String user2Id);

    @Query("SELECT d FROM DmConversation d WHERE d.user1Id = :userId OR d.user2Id = :userId")
    List<DmConversation> findByUserId(String userId);
}

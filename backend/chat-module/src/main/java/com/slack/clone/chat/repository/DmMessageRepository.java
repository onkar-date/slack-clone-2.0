package com.slack.clone.chat.repository;

import com.slack.clone.chat.document.DmMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for DmMessage documents
 */
@Repository
public interface DmMessageRepository extends MongoRepository<DmMessage, String> {

    Page<DmMessage> findByConversationIdOrderByCreatedAtDesc(String conversationId, Pageable pageable);
}

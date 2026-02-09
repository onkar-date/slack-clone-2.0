package com.slack.clone.chat.repository;

import com.slack.clone.chat.document.ChannelMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for ChannelMessage documents
 */
@Repository
public interface ChannelMessageRepository extends MongoRepository<ChannelMessage, String> {

    Page<ChannelMessage> findByChannelIdOrderByCreatedAtDesc(String channelId, Pageable pageable);
}

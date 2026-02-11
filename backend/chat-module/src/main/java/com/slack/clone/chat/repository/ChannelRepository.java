package com.slack.clone.chat.repository;

import com.slack.clone.chat.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Channel entity
 */
@Repository
public interface ChannelRepository extends JpaRepository<Channel, String> {

    Optional<Channel> findByName(String name);

    boolean existsByName(String name);

    @Query("SELECT c FROM Channel c JOIN c.members m WHERE m.userId = :userId")
    List<Channel> findByUserId(String userId);
}

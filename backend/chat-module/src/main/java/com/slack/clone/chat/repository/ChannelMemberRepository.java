package com.slack.clone.chat.repository;

import com.slack.clone.chat.entity.ChannelMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ChannelMember entity
 */
@Repository
public interface ChannelMemberRepository extends JpaRepository<ChannelMember, String> {

    List<ChannelMember> findByChannelId(String channelId);

    List<ChannelMember> findByUserId(String userId);

    Optional<ChannelMember> findByChannelIdAndUserId(String channelId, String userId);

    boolean existsByChannelIdAndUserId(String channelId, String userId);

    void deleteByChannelIdAndUserId(String channelId, String userId);
}

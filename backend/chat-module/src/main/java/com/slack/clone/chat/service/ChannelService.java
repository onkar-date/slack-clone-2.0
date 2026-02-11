package com.slack.clone.chat.service;

import com.slack.clone.chat.dto.ChannelDTO;
import com.slack.clone.chat.dto.CreateChannelRequest;
import com.slack.clone.chat.entity.Channel;
import com.slack.clone.chat.entity.ChannelMember;
import com.slack.clone.chat.mapper.ChatMapper;
import com.slack.clone.chat.repository.ChannelMemberRepository;
import com.slack.clone.chat.repository.ChannelRepository;
import com.slack.clone.shared.exception.ResourceNotFoundException;
import com.slack.clone.shared.exception.ValidationException;
import com.slack.clone.shared.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for channel management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private final ChatMapper chatMapper;

    /**
     * Create a new channel
     */
    @Transactional
    public ChannelDTO createChannel(CreateChannelRequest request, String createdBy) {
        log.info("Creating channel: {} by user: {}", request.getName(), createdBy);

        if (channelRepository.existsByName(request.getName())) {
            throw new ValidationException("Channel name already exists: " + request.getName());
        }

        Channel channel = Channel.builder()
                .id(IdGenerator.generateId())
                .name(request.getName())
                .description(request.getDescription())
                .createdBy(createdBy)
                .build();

        Channel savedChannel = channelRepository.save(channel);

        // Add creator as member
        addMemberToChannel(savedChannel.getId(), createdBy);

        log.info("Channel created: {}", savedChannel.getId());
        return chatMapper.toDTO(savedChannel);
    }

    /**
     * Get all channels
     */
    @Transactional(readOnly = true)
    public List<ChannelDTO> getAllChannels() {
        return channelRepository.findAll().stream()
                .map(chatMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get channel by ID
     */
    @Transactional(readOnly = true)
    public ChannelDTO getChannelById(String channelId) {
        Channel channel = findChannelOrThrow(channelId);
        return chatMapper.toDTO(channel);
    }

    /**
     * Get channels for a user
     */
    @Transactional(readOnly = true)
    public List<ChannelDTO> getChannelsForUser(String userId) {
        return channelRepository.findByUserId(userId).stream()
                .map(chatMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Add member to channel
     */
    @Transactional
    public void addMemberToChannel(String channelId, String userId) {
        log.info("Adding user {} to channel {}", userId, channelId);

        Channel channel = findChannelOrThrow(channelId);

        if (channelMemberRepository.existsByChannelIdAndUserId(channelId, userId)) {
            throw new ValidationException("User already a member of this channel");
        }

        ChannelMember member = ChannelMember.builder()
                .id(IdGenerator.generateId())
                .channel(channel)
                .userId(userId)
                .build();

        channelMemberRepository.save(member);
        log.info("User {} added to channel {}", userId, channelId);
    }

    /**
     * Remove member from channel
     */
    @Transactional
    public void removeMemberFromChannel(String channelId, String userId) {
        log.info("Removing user {} from channel {}", userId, channelId);

        findChannelOrThrow(channelId);

        if (!channelMemberRepository.existsByChannelIdAndUserId(channelId, userId)) {
            throw new ValidationException("User is not a member of this channel");
        }

        channelMemberRepository.deleteByChannelIdAndUserId(channelId, userId);
        log.info("User {} removed from channel {}", userId, channelId);
    }

    /**
     * Check if user is member of channel
     */
    @Transactional(readOnly = true)
    public boolean isMember(String channelId, String userId) {
        return channelMemberRepository.existsByChannelIdAndUserId(channelId, userId);
    }

    private Channel findChannelOrThrow(String channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel", channelId));
    }
}

package com.slack.clone.chat.service;

import com.slack.clone.chat.document.ChannelMessage;
import com.slack.clone.chat.document.DmMessage;
import com.slack.clone.chat.dto.MessageDTO;
import com.slack.clone.chat.dto.SendMessageRequest;
import com.slack.clone.chat.mapper.ChatMapper;
import com.slack.clone.chat.repository.ChannelMessageRepository;
import com.slack.clone.chat.repository.DmMessageRepository;
import com.slack.clone.shared.exception.UnauthorizedException;
import com.slack.clone.shared.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service for messaging
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MessagingService {

    private final ChannelMessageRepository channelMessageRepository;
    private final DmMessageRepository dmMessageRepository;
    private final ChannelService channelService;
    private final DmService dmService;
    private final ChatMapper chatMapper;

    /**
     * Send message to channel
     */
    public MessageDTO sendChannelMessage(String channelId, SendMessageRequest request, String senderId) {
        log.info("Sending message to channel {} by user {}", channelId, senderId);

        // Validate membership
        if (!channelService.isMember(channelId, senderId)) {
            throw new UnauthorizedException("User is not a member of this channel");
        }

        ChannelMessage message = ChannelMessage.builder()
                .id(IdGenerator.generateId())
                .channelId(channelId)
                .senderId(senderId)
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        ChannelMessage saved = channelMessageRepository.save(message);
        log.info("Channel message sent: {}", saved.getId());

        return chatMapper.toDTO(saved);
    }

    /**
     * Get channel message history
     */
    public Page<MessageDTO> getChannelMessages(String channelId, String userId, Pageable pageable) {
        // Validate membership
        if (!channelService.isMember(channelId, userId)) {
            throw new UnauthorizedException("User is not a member of this channel");
        }

        return channelMessageRepository.findByChannelIdOrderByCreatedAtDesc(channelId, pageable)
                .map(chatMapper::toDTO);
    }

    /**
     * Send DM message
     */
    public MessageDTO sendDmMessage(String conversationId, SendMessageRequest request, String senderId) {
        log.info("Sending DM to conversation {} by user {}", conversationId, senderId);

        // Validate participation
        if (!dmService.isParticipant(conversationId, senderId)) {
            throw new UnauthorizedException("User is not a participant in this conversation");
        }

        DmMessage message = DmMessage.builder()
                .id(IdGenerator.generateId())
                .conversationId(conversationId)
                .senderId(senderId)
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        DmMessage saved = dmMessageRepository.save(message);
        log.info("DM message sent: {}", saved.getId());

        return chatMapper.toDTO(saved);
    }

    /**
     * Get DM message history
     */
    public Page<MessageDTO> getDmMessages(String conversationId, String userId, Pageable pageable) {
        // Validate participation
        if (!dmService.isParticipant(conversationId, userId)) {
            throw new UnauthorizedException("User is not a participant in this conversation");
        }

        return dmMessageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable)
                .map(chatMapper::toDTO);
    }
}

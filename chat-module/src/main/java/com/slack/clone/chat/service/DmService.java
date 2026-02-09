package com.slack.clone.chat.service;

import com.slack.clone.chat.dto.CreateDmRequest;
import com.slack.clone.chat.dto.DmConversationDTO;
import com.slack.clone.chat.entity.DmConversation;
import com.slack.clone.chat.mapper.ChatMapper;
import com.slack.clone.chat.repository.DmConversationRepository;
import com.slack.clone.shared.exception.ResourceNotFoundException;
import com.slack.clone.shared.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for DM conversation management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DmService {

    private final DmConversationRepository dmConversationRepository;
    private final ChatMapper chatMapper;

    /**
     * Create or get existing DM conversation
     */
    @Transactional
    public DmConversationDTO createOrGetDmConversation(CreateDmRequest request, String currentUserId) {
        log.info("Creating/getting DM between {} and {}", currentUserId, request.getPeerUserId());

        // Ensure user1Id < user2Id for consistency
        String user1Id = currentUserId.compareTo(request.getPeerUserId()) < 0
                ? currentUserId
                : request.getPeerUserId();
        String user2Id = currentUserId.compareTo(request.getPeerUserId()) < 0
                ? request.getPeerUserId()
                : currentUserId;

        // Check if conversation already exists
        return dmConversationRepository.findByUserPair(user1Id, user2Id)
                .map(chatMapper::toDTO)
                .orElseGet(() -> {
                    DmConversation conversation = DmConversation.builder()
                            .id(IdGenerator.generateId())
                            .user1Id(user1Id)
                            .user2Id(user2Id)
                            .build();

                    DmConversation saved = dmConversationRepository.save(conversation);
                    log.info("DM conversation created: {}", saved.getId());
                    return chatMapper.toDTO(saved);
                });
    }

    /**
     * Get all DM conversations for a user
     */
    @Transactional(readOnly = true)
    public List<DmConversationDTO> getDmConversationsForUser(String userId) {
        return dmConversationRepository.findByUserId(userId).stream()
                .map(chatMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get DM conversation by ID
     */
    @Transactional(readOnly = true)
    public DmConversationDTO getDmConversationById(String conversationId) {
        DmConversation conversation = findConversationOrThrow(conversationId);
        return chatMapper.toDTO(conversation);
    }

    /**
     * Check if user is participant in conversation
     */
    @Transactional(readOnly = true)
    public boolean isParticipant(String conversationId, String userId) {
        DmConversation conversation = findConversationOrThrow(conversationId);
        return conversation.getUser1Id().equals(userId) || conversation.getUser2Id().equals(userId);
    }

    private DmConversation findConversationOrThrow(String conversationId) {
        return dmConversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("DM Conversation", conversationId));
    }
}

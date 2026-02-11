package com.slack.clone.chat.service;

import com.slack.clone.chat.document.ChannelMessage;
import com.slack.clone.chat.dto.MessageDTO;
import com.slack.clone.chat.dto.SendMessageRequest;
import com.slack.clone.chat.mapper.ChatMapper;
import com.slack.clone.chat.repository.ChannelMessageRepository;
import com.slack.clone.shared.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MessagingService
 */
@ExtendWith(MockitoExtension.class)
class MessagingServiceTest {

    @Mock
    private ChannelMessageRepository channelMessageRepository;

    @Mock
    private ChannelService channelService;

    @Mock
    private ChatMapper chatMapper;

    @InjectMocks
    private MessagingService messagingService;

    private String channelId;
    private String senderId;
    private SendMessageRequest sendRequest;
    private ChannelMessage message;
    private MessageDTO messageDTO;

    @BeforeEach
    void setUp() {
        channelId = "channel-123";
        senderId = "user-123";

        sendRequest = new SendMessageRequest();
        sendRequest.setContent("Hello, world!");

        message = ChannelMessage.builder()
                .id("msg-123")
                .channelId(channelId)
                .senderId(senderId)
                .content("Hello, world!")
                .createdAt(LocalDateTime.now())
                .type("TEXT")
                .build();

        messageDTO = MessageDTO.builder()
                .id("msg-123")
                .senderId(senderId)
                .content("Hello, world!")
                .type("TEXT")
                .build();
    }

    @Test
    @Disabled
    void shouldSendChannelMessage() {
        // Given
        when(channelService.isMember(channelId, senderId)).thenReturn(true);
        when(channelMessageRepository.save(any(ChannelMessage.class))).thenReturn(message);
        when(chatMapper.toDTO(message)).thenReturn(messageDTO);

        // When
        MessageDTO result = messagingService.sendChannelMessage(channelId, sendRequest, senderId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo(sendRequest.getContent());
        assertThat(result.getSenderId()).isEqualTo(senderId);

        verify(channelService).isMember(channelId, senderId);
        verify(channelMessageRepository).save(any(ChannelMessage.class));
    }

    @Test
    @Disabled
    void shouldThrowExceptionWhenUserNotMember() {
        // Given
        when(channelService.isMember(channelId, senderId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> messagingService.sendChannelMessage(channelId, sendRequest, senderId))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("not a member");

        verify(channelService).isMember(channelId, senderId);
        verify(channelMessageRepository, never()).save(any(ChannelMessage.class));
    }

    @Test
    @Disabled
    void shouldGetChannelMessages() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        List<ChannelMessage> messages = List.of(message);
        Page<ChannelMessage> messagePage = new PageImpl<>(messages, pageable, 1);

        when(channelService.isMember(channelId, senderId)).thenReturn(true);
        when(channelMessageRepository.findByChannelIdOrderByCreatedAtDesc(channelId, pageable))
                .thenReturn(messagePage);
        when(chatMapper.toDTO(message)).thenReturn(messageDTO);

        // When
        Page<MessageDTO> result = messagingService.getChannelMessages(channelId, senderId, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getContent()).isEqualTo("Hello, world!");

        verify(channelService).isMember(channelId, senderId);
        verify(channelMessageRepository).findByChannelIdOrderByCreatedAtDesc(channelId, pageable);
    }

    @Test
    @Disabled
    void shouldThrowExceptionWhenGettingMessagesForNonMember() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        when(channelService.isMember(channelId, senderId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> messagingService.getChannelMessages(channelId, senderId, pageable))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("not a member");

        verify(channelService).isMember(channelId, senderId);
        verify(channelMessageRepository, never()).findByChannelIdOrderByCreatedAtDesc(any(), any());
    }
}

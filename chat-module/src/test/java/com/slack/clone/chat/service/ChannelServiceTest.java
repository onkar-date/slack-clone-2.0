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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ChannelService
 */
@ExtendWith(MockitoExtension.class)
class ChannelServiceTest {

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private ChannelMemberRepository channelMemberRepository;

    @Mock
    private ChatMapper chatMapper;

    @InjectMocks
    private ChannelService channelService;

    private CreateChannelRequest createRequest;
    private Channel channel;
    private ChannelDTO channelDTO;
    private String userId;

    @BeforeEach
    void setUp() {
        userId = "user-123";

        createRequest = new CreateChannelRequest();
        createRequest.setName("general");
        createRequest.setDescription("General discussion");

        channel = Channel.builder()
                .id("channel-123")
                .name("general")
                .description("General discussion")
                .createdBy(userId)
                .members(new ArrayList<>())
                .build();

        channelDTO = ChannelDTO.builder()
                .id("channel-123")
                .name("general")
                .description("General discussion")
                .createdBy(userId)
                .memberCount(1)
                .build();
    }

    @Test
    void shouldCreateChannel() {
        // Given
        when(channelRepository.existsByName(createRequest.getName())).thenReturn(false);
        when(channelRepository.save(any(Channel.class))).thenReturn(channel);
        when(channelMemberRepository.save(any(ChannelMember.class))).thenReturn(new ChannelMember());
        when(chatMapper.toDTO(channel)).thenReturn(channelDTO);

        // When
        ChannelDTO result = channelService.createChannel(createRequest, userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(createRequest.getName());
        assertThat(result.getCreatedBy()).isEqualTo(userId);

        verify(channelRepository).existsByName(createRequest.getName());
        verify(channelRepository).save(any(Channel.class));
        verify(channelMemberRepository).save(any(ChannelMember.class));
    }

    @Test
    void shouldThrowExceptionWhenChannelNameExists() {
        // Given
        when(channelRepository.existsByName(createRequest.getName())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> channelService.createChannel(createRequest, userId))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Channel name already exists");

        verify(channelRepository).existsByName(createRequest.getName());
        verify(channelRepository, never()).save(any(Channel.class));
    }

    @Test
    void shouldGetAllChannels() {
        // Given
        List<Channel> channels = List.of(channel);
        when(channelRepository.findAll()).thenReturn(channels);
        when(chatMapper.toDTO(channel)).thenReturn(channelDTO);

        // When
        List<ChannelDTO> result = channelService.getAllChannels();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("general");

        verify(channelRepository).findAll();
    }

    @Test
    void shouldAddMemberToChannel() {
        // Given
        String newUserId = "user-456";
        when(channelRepository.findById(channel.getId())).thenReturn(Optional.of(channel));
        when(channelMemberRepository.existsByChannelIdAndUserId(channel.getId(), newUserId)).thenReturn(false);
        when(channelMemberRepository.save(any(ChannelMember.class))).thenReturn(new ChannelMember());

        // When
        channelService.addMemberToChannel(channel.getId(), newUserId);

        // Then
        verify(channelRepository).findById(channel.getId());
        verify(channelMemberRepository).existsByChannelIdAndUserId(channel.getId(), newUserId);
        verify(channelMemberRepository).save(any(ChannelMember.class));
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyMember() {
        // Given
        when(channelRepository.findById(channel.getId())).thenReturn(Optional.of(channel));
        when(channelMemberRepository.existsByChannelIdAndUserId(channel.getId(), userId)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> channelService.addMemberToChannel(channel.getId(), userId))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("already a member");

        verify(channelMemberRepository, never()).save(any(ChannelMember.class));
    }

    @Test
    void shouldThrowExceptionWhenChannelNotFound() {
        // Given
        String invalidChannelId = "invalid-id";
        when(channelRepository.findById(invalidChannelId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> channelService.getChannelById(invalidChannelId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Channel");

        verify(channelRepository).findById(invalidChannelId);
    }

    @Test
    void shouldCheckIfUserIsMember() {
        // Given
        when(channelMemberRepository.existsByChannelIdAndUserId(channel.getId(), userId)).thenReturn(true);

        // When
        boolean isMember = channelService.isMember(channel.getId(), userId);

        // Then
        assertThat(isMember).isTrue();
        verify(channelMemberRepository).existsByChannelIdAndUserId(channel.getId(), userId);
    }
}

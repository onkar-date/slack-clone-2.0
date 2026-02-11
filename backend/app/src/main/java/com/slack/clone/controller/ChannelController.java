package com.slack.clone.controller;

import com.slack.clone.chat.dto.ChannelDTO;
import com.slack.clone.chat.dto.CreateChannelRequest;
import com.slack.clone.chat.dto.MessageDTO;
import com.slack.clone.chat.dto.SendMessageRequest;
import com.slack.clone.chat.service.ChannelService;
import com.slack.clone.chat.service.MessagingService;
import com.slack.clone.identity.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for channel operations
 */
@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-jwt")
@Tag(name = "Channels", description = "Channel management APIs")
public class ChannelController {

    private final ChannelService channelService;
    private final MessagingService messagingService;
    private final JwtService jwtService;

    @PostMapping
    @Operation(summary = "Create a new channel")
    public ResponseEntity<ChannelDTO> createChannel(
            @Valid @RequestBody CreateChannelRequest request,
            @RequestHeader("Authorization") String authHeader) {
        String userId = extractUserId(authHeader);
        ChannelDTO channel = channelService.createChannel(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(channel);
    }

    @GetMapping
    @Operation(summary = "Get all channels")
    public ResponseEntity<List<ChannelDTO>> getAllChannels() {
        List<ChannelDTO> channels = channelService.getAllChannels();
        return ResponseEntity.ok(channels);
    }

    @GetMapping("/{channelId}")
    @Operation(summary = "Get channel by ID")
    public ResponseEntity<ChannelDTO> getChannel(@PathVariable String channelId) {
        ChannelDTO channel = channelService.getChannelById(channelId);
        return ResponseEntity.ok(channel);
    }

    @PostMapping("/{channelId}/join")
    @Operation(summary = "Join a channel")
    public ResponseEntity<Void> joinChannel(
            @PathVariable String channelId,
            @RequestHeader("Authorization") String authHeader) {
        String userId = extractUserId(authHeader);
        channelService.addMemberToChannel(channelId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{channelId}/leave")
    @Operation(summary = "Leave a channel")
    public ResponseEntity<Void> leaveChannel(
            @PathVariable String channelId,
            @RequestHeader("Authorization") String authHeader) {
        String userId = extractUserId(authHeader);
        channelService.removeMemberFromChannel(channelId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{channelId}/messages")
    @Operation(summary = "Get channel message history")
    public ResponseEntity<Page<MessageDTO>> getChannelMessages(
            @PathVariable String channelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader("Authorization") String authHeader) {
        String userId = extractUserId(authHeader);
        Pageable pageable = PageRequest.of(page, size);
        Page<MessageDTO> messages = messagingService.getChannelMessages(channelId, userId, pageable);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/{channelId}/messages")
    @Operation(summary = "Send a message to channel")
    public ResponseEntity<MessageDTO> sendMessage(
            @PathVariable String channelId,
            @Valid @RequestBody SendMessageRequest request,
            @RequestHeader("Authorization") String authHeader) {
        String userId = extractUserId(authHeader);
        MessageDTO message = messagingService.sendChannelMessage(channelId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    private String extractUserId(String authHeader) {
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        return jwtService.extractUserId(token);
    }
}

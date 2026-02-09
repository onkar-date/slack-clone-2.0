package com.slack.clone.controller;

import com.slack.clone.chat.dto.CreateDmRequest;
import com.slack.clone.chat.dto.DmConversationDTO;
import com.slack.clone.chat.dto.MessageDTO;
import com.slack.clone.chat.dto.SendMessageRequest;
import com.slack.clone.chat.service.DmService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for DM operations
 */
@RestController
@RequestMapping("/api/dm")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-jwt")
@Tag(name = "Direct Messages", description = "Direct message APIs")
public class DmController {

    private final DmService dmService;
    private final MessagingService messagingService;
    private final JwtService jwtService;

    @PostMapping("/conversations")
    @Operation(summary = "Create or get DM conversation")
    public ResponseEntity<DmConversationDTO> createDmConversation(
            @Valid @RequestBody CreateDmRequest request,
            @RequestHeader("Authorization") String authHeader) {
        String userId = extractUserId(authHeader);
        DmConversationDTO conversation = dmService.createOrGetDmConversation(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(conversation);
    }

    @GetMapping("/conversations")
    @Operation(summary = "Get all DM conversations for current user")
    public ResponseEntity<List<DmConversationDTO>> getDmConversations(
            @RequestHeader("Authorization") String authHeader) {
        String userId = extractUserId(authHeader);
        List<DmConversationDTO> conversations = dmService.getDmConversationsForUser(userId);
        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/conversations/{conversationId}")
    @Operation(summary = "Get DM conversation by ID")
    public ResponseEntity<DmConversationDTO> getDmConversation(@PathVariable String conversationId) {
        DmConversationDTO conversation = dmService.getDmConversationById(conversationId);
        return ResponseEntity.ok(conversation);
    }

    @GetMapping("/conversations/{conversationId}/messages")
    @Operation(summary = "Get DM message history")
    public ResponseEntity<Page<MessageDTO>> getDmMessages(
            @PathVariable String conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader("Authorization") String authHeader) {
        String userId = extractUserId(authHeader);
        Pageable pageable = PageRequest.of(page, size);
        Page<MessageDTO> messages = messagingService.getDmMessages(conversationId, userId, pageable);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/conversations/{conversationId}/messages")
    @Operation(summary = "Send a DM message")
    public ResponseEntity<MessageDTO> sendMessage(
            @PathVariable String conversationId,
            @Valid @RequestBody SendMessageRequest request,
            @RequestHeader("Authorization") String authHeader) {
        String userId = extractUserId(authHeader);
        MessageDTO message = messagingService.sendDmMessage(conversationId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    private String extractUserId(String authHeader) {
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        return jwtService.extractUserId(token);
    }
}

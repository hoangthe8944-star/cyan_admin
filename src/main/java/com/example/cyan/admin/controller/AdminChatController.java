package com.example.cyan.admin.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cyan.chat.dto.AdminChatReplyRequest;
import com.example.cyan.chat.dto.AssignChatConversationRequest;
import com.example.cyan.chat.dto.ChatConversationDetailResponse;
import com.example.cyan.chat.dto.ChatConversationSummaryResponse;
import com.example.cyan.chat.dto.UpdateChatConversationStatusRequest;
import com.example.cyan.chat.service.ChatService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/admin/chat/conversations")
public class AdminChatController {

    private final ChatService chatService;

    public AdminChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping
    public List<ChatConversationSummaryResponse> findAll() {
        return chatService.findAll();
    }

    @GetMapping("/{id}")
    public ChatConversationDetailResponse findById(@PathVariable String id) {
        return chatService.findDetailById(id);
    }

    @PostMapping("/{id}/reply")
    public ChatConversationDetailResponse reply(@PathVariable String id,
            @Valid @RequestBody AdminChatReplyRequest request) {
        return chatService.replyAsAdmin(id, request.getAdminName(), request.getMessage());
    }

    @PatchMapping("/{id}/assign")
    public ChatConversationDetailResponse assign(@PathVariable String id,
            @Valid @RequestBody AssignChatConversationRequest request) {
        return chatService.assignAdmin(id, request.getAdminName());
    }

    @PatchMapping("/{id}/status")
    public ChatConversationDetailResponse updateStatus(@PathVariable String id,
            @Valid @RequestBody UpdateChatConversationStatusRequest request) {
        return chatService.updateStatus(id, request.getStatus());
    }

    @PatchMapping("/{id}/read")
    public ChatConversationDetailResponse markRead(@PathVariable String id) {
        return chatService.markReadByAdmin(id);
    }
}

package com.example.cyan.chat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.cyan.chat.dto.ChatConversationDetailResponse;
import com.example.cyan.chat.dto.ChatConversationSummaryResponse;
import com.example.cyan.chat.dto.ChatMessageRequest;
import com.example.cyan.chat.dto.CreateChatConversationRequest;
import com.example.cyan.chat.service.ChatService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/chat/conversations")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChatConversationDetailResponse createConversation(@Valid @RequestBody CreateChatConversationRequest request) {
        return chatService.createConversation(request);
    }

    @GetMapping
    public java.util.List<ChatConversationSummaryResponse> findByCustomerUserId(@RequestParam String customerUserId) {
        return chatService.findByCustomerUserId(customerUserId);
    }

    @GetMapping("/{id}")
    public ChatConversationDetailResponse findById(@PathVariable String id) {
        return chatService.findDetailById(id);
    }

    @GetMapping("/customer/{customerUserId}/{id}")
    public ChatConversationDetailResponse findByIdForCustomer(@PathVariable String customerUserId,
            @PathVariable String id) {
        return chatService.findDetailByIdForCustomer(customerUserId, id);
    }

    @GetMapping("/code/{conversationCode}")
    public ChatConversationDetailResponse findByCode(@PathVariable String conversationCode) {
        return chatService.findDetailByCode(conversationCode);
    }

    @PostMapping("/{id}/messages")
    public ChatConversationDetailResponse addMessage(@PathVariable String id,
            @Valid @RequestBody ChatMessageRequest request) {
        return chatService.addCustomerMessage(id, request.getMessage());
    }

    @PostMapping("/customer/{customerUserId}/{id}/messages")
    public ChatConversationDetailResponse addMessageForCustomer(@PathVariable String customerUserId,
            @PathVariable String id,
            @Valid @RequestBody ChatMessageRequest request) {
        return chatService.addCustomerMessageForCustomer(customerUserId, id, request.getMessage());
    }

    @PatchMapping("/{id}/read")
    public ChatConversationDetailResponse markRead(@PathVariable String id) {
        return chatService.markReadByCustomer(id);
    }

    @PatchMapping("/customer/{customerUserId}/{id}/read")
    public ChatConversationDetailResponse markReadForCustomer(@PathVariable String customerUserId,
            @PathVariable String id) {
        return chatService.markReadByCustomer(customerUserId, id);
    }
}

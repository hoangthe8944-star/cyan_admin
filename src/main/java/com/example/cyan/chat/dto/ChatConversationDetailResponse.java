package com.example.cyan.chat.dto;

import java.time.Instant;
import java.util.List;

import com.example.cyan.common.model.enums.ChatConversationStatus;

public record ChatConversationDetailResponse(
        String id,
        String conversationCode,
        String customerName,
        String customerEmail,
        String customerPhone,
        String subject,
        ChatConversationStatus status,
        String assignedAdminName,
        Instant lastMessageAt,
        String lastMessagePreview,
        int unreadAdminCount,
        int unreadCustomerCount,
        Instant closedAt,
        Instant createdAt,
        Instant updatedAt,
        List<ChatMessageResponse> messages) {
}

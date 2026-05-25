package com.example.cyan.chat.dto;

import java.time.Instant;

import com.example.cyan.common.model.enums.ChatSenderType;

public record ChatMessageResponse(
        ChatSenderType senderType,
        String senderName,
        String senderEmail,
        String senderPhone,
        String content,
        Instant sentAt,
        Instant readByAdminAt,
        Instant readByCustomerAt) {
}

package com.example.cyan.chat.dto;

import com.example.cyan.common.model.enums.ChatConversationStatus;

import jakarta.validation.constraints.NotNull;

public class UpdateChatConversationStatusRequest {

    @NotNull
    private ChatConversationStatus status;

    public ChatConversationStatus getStatus() {
        return status;
    }

    public void setStatus(ChatConversationStatus status) {
        this.status = status;
    }
}

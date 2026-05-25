package com.example.cyan.chat.model;

import java.time.Instant;

import com.example.cyan.common.model.enums.ChatSenderType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ChatMessage {

    @NotNull
    private ChatSenderType senderType;

    @Size(max = 120)
    private String senderName;

    @Size(max = 160)
    private String senderEmail;

    @Size(max = 30)
    private String senderPhone;

    @NotBlank
    @Size(max = 2000)
    private String content;

    @NotNull
    private Instant sentAt;

    private Instant readByAdminAt;

    private Instant readByCustomerAt;

    public ChatSenderType getSenderType() {
        return senderType;
    }

    public void setSenderType(ChatSenderType senderType) {
        this.senderType = senderType;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    public Instant getReadByAdminAt() {
        return readByAdminAt;
    }

    public void setReadByAdminAt(Instant readByAdminAt) {
        this.readByAdminAt = readByAdminAt;
    }

    public Instant getReadByCustomerAt() {
        return readByCustomerAt;
    }

    public void setReadByCustomerAt(Instant readByCustomerAt) {
        this.readByCustomerAt = readByCustomerAt;
    }
}

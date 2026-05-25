package com.example.cyan.chat.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.cyan.common.model.BaseDocument;
import com.example.cyan.common.model.enums.ChatConversationStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Document("chat_conversations")
public class ChatConversation extends BaseDocument {

    @Indexed(unique = true)
    @NotBlank
    @Size(max = 60)
    private String conversationCode;

    @NotBlank
    @Size(max = 120)
    private String customerName;

    @Email
    @Size(max = 160)
    private String customerEmail;

    @Size(max = 30)
    private String customerPhone;

    @Size(max = 200)
    private String subject;

    @NotNull
    private ChatConversationStatus status = ChatConversationStatus.OPEN;

    @Size(max = 120)
    private String assignedAdminName;

    private Instant lastMessageAt;

    @Size(max = 240)
    private String lastMessagePreview;

    private int unreadAdminCount;

    private int unreadCustomerCount;

    private Instant closedAt;

    @Valid
    private List<ChatMessage> messages = new ArrayList<>();

    public String getConversationCode() {
        return conversationCode;
    }

    public void setConversationCode(String conversationCode) {
        this.conversationCode = conversationCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public ChatConversationStatus getStatus() {
        return status;
    }

    public void setStatus(ChatConversationStatus status) {
        this.status = status;
    }

    public String getAssignedAdminName() {
        return assignedAdminName;
    }

    public void setAssignedAdminName(String assignedAdminName) {
        this.assignedAdminName = assignedAdminName;
    }

    public Instant getLastMessageAt() {
        return lastMessageAt;
    }

    public void setLastMessageAt(Instant lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }

    public String getLastMessagePreview() {
        return lastMessagePreview;
    }

    public void setLastMessagePreview(String lastMessagePreview) {
        this.lastMessagePreview = lastMessagePreview;
    }

    public int getUnreadAdminCount() {
        return unreadAdminCount;
    }

    public void setUnreadAdminCount(int unreadAdminCount) {
        this.unreadAdminCount = unreadAdminCount;
    }

    public int getUnreadCustomerCount() {
        return unreadCustomerCount;
    }

    public void setUnreadCustomerCount(int unreadCustomerCount) {
        this.unreadCustomerCount = unreadCustomerCount;
    }

    public Instant getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(Instant closedAt) {
        this.closedAt = closedAt;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }
}

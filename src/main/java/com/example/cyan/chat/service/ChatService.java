package com.example.cyan.chat.service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.cyan.chat.dto.ChatConversationDetailResponse;
import com.example.cyan.chat.dto.ChatConversationSummaryResponse;
import com.example.cyan.chat.dto.ChatMessageResponse;
import com.example.cyan.chat.dto.CreateChatConversationRequest;
import com.example.cyan.chat.model.ChatConversation;
import com.example.cyan.chat.model.ChatMessage;
import com.example.cyan.chat.repository.ChatConversationRepository;
import com.example.cyan.common.exception.BadRequestException;
import com.example.cyan.common.exception.ResourceNotFoundException;
import com.example.cyan.common.model.enums.ChatConversationStatus;
import com.example.cyan.common.model.enums.ChatSenderType;

@Service
public class ChatService {

    private static final int PREVIEW_LENGTH = 240;

    private final ChatConversationRepository chatConversationRepository;

    public ChatService(ChatConversationRepository chatConversationRepository) {
        this.chatConversationRepository = chatConversationRepository;
    }

    public ChatConversationDetailResponse createConversation(CreateChatConversationRequest request) {
        ChatConversation conversation = new ChatConversation();
        conversation.setConversationCode(generateConversationCode());
        conversation.setCustomerName(request.getCustomerName());
        conversation.setCustomerEmail(request.getCustomerEmail());
        conversation.setCustomerPhone(request.getCustomerPhone());
        conversation.setSubject(request.getSubject());
        conversation.setStatus(ChatConversationStatus.PENDING_ADMIN);

        appendMessage(conversation, buildCustomerMessage(
                request.getCustomerName(),
                request.getCustomerEmail(),
                request.getCustomerPhone(),
                request.getMessage()));

        return toDetailResponse(chatConversationRepository.save(conversation));
    }

    public List<ChatConversationSummaryResponse> findAll() {
        return chatConversationRepository.findAll(
                Sort.by(Sort.Order.desc("lastMessageAt"), Sort.Order.desc("createdAt")))
                .stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    public ChatConversationDetailResponse findDetailById(String id) {
        return toDetailResponse(findEntityById(id));
    }

    public ChatConversationDetailResponse findDetailByCode(String conversationCode) {
        return toDetailResponse(findEntityByCode(conversationCode));
    }

    public ChatConversationDetailResponse addCustomerMessage(String conversationId, String message) {
        ChatConversation conversation = findEntityById(conversationId);
        ensureConversationOpen(conversation);
        appendMessage(conversation, buildCustomerMessage(
                conversation.getCustomerName(),
                conversation.getCustomerEmail(),
                conversation.getCustomerPhone(),
                message));
        conversation.setStatus(ChatConversationStatus.PENDING_ADMIN);
        return toDetailResponse(chatConversationRepository.save(conversation));
    }

    public ChatConversationDetailResponse replyAsAdmin(String conversationId, String adminName, String message) {
        ChatConversation conversation = findEntityById(conversationId);
        ensureConversationOpen(conversation);
        conversation.setAssignedAdminName(adminName);
        appendMessage(conversation, buildAdminMessage(adminName, message));
        conversation.setStatus(ChatConversationStatus.ANSWERED);
        return toDetailResponse(chatConversationRepository.save(conversation));
    }

    public ChatConversationDetailResponse assignAdmin(String conversationId, String adminName) {
        ChatConversation conversation = findEntityById(conversationId);
        conversation.setAssignedAdminName(adminName);
        return toDetailResponse(chatConversationRepository.save(conversation));
    }

    public ChatConversationDetailResponse updateStatus(String conversationId, ChatConversationStatus status) {
        ChatConversation conversation = findEntityById(conversationId);
        conversation.setStatus(status);
        conversation.setClosedAt(status == ChatConversationStatus.CLOSED ? Instant.now() : null);
        return toDetailResponse(chatConversationRepository.save(conversation));
    }

    public ChatConversationDetailResponse markReadByAdmin(String conversationId) {
        ChatConversation conversation = findEntityById(conversationId);
        Instant now = Instant.now();
        conversation.getMessages().stream()
                .filter(message -> message.getSenderType() == ChatSenderType.CUSTOMER)
                .filter(message -> message.getReadByAdminAt() == null)
                .forEach(message -> message.setReadByAdminAt(now));
        conversation.setUnreadAdminCount(0);
        return toDetailResponse(chatConversationRepository.save(conversation));
    }

    public ChatConversationDetailResponse markReadByCustomer(String conversationId) {
        ChatConversation conversation = findEntityById(conversationId);
        Instant now = Instant.now();
        conversation.getMessages().stream()
                .filter(message -> message.getSenderType() == ChatSenderType.ADMIN)
                .filter(message -> message.getReadByCustomerAt() == null)
                .forEach(message -> message.setReadByCustomerAt(now));
        conversation.setUnreadCustomerCount(0);
        return toDetailResponse(chatConversationRepository.save(conversation));
    }

    private ChatConversation findEntityById(String id) {
        return chatConversationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chat conversation not found: " + id));
    }

    private ChatConversation findEntityByCode(String conversationCode) {
        return chatConversationRepository.findByConversationCode(conversationCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chat conversation not found with code: " + conversationCode));
    }

    private void ensureConversationOpen(ChatConversation conversation) {
        if (conversation.getStatus() == ChatConversationStatus.CLOSED) {
            throw new BadRequestException("Chat conversation is closed");
        }
    }

    private ChatMessage buildCustomerMessage(String customerName, String customerEmail, String customerPhone, String content) {
        ChatMessage message = new ChatMessage();
        message.setSenderType(ChatSenderType.CUSTOMER);
        message.setSenderName(customerName);
        message.setSenderEmail(customerEmail);
        message.setSenderPhone(customerPhone);
        message.setContent(content);
        message.setSentAt(Instant.now());
        return message;
    }

    private ChatMessage buildAdminMessage(String adminName, String content) {
        ChatMessage message = new ChatMessage();
        message.setSenderType(ChatSenderType.ADMIN);
        message.setSenderName(adminName);
        message.setContent(content);
        message.setSentAt(Instant.now());
        message.setReadByAdminAt(Instant.now());
        return message;
    }

    private void appendMessage(ChatConversation conversation, ChatMessage message) {
        conversation.getMessages().add(message);
        conversation.getMessages().sort(Comparator.comparing(ChatMessage::getSentAt));
        conversation.setLastMessageAt(message.getSentAt());
        conversation.setLastMessagePreview(buildPreview(message.getContent()));

        if (message.getSenderType() == ChatSenderType.CUSTOMER) {
            conversation.setUnreadAdminCount(conversation.getUnreadAdminCount() + 1);
        } else {
            conversation.setUnreadCustomerCount(conversation.getUnreadCustomerCount() + 1);
        }
    }

    private String buildPreview(String content) {
        if (content == null || content.length() <= PREVIEW_LENGTH) {
            return content;
        }
        return content.substring(0, PREVIEW_LENGTH - 3) + "...";
    }

    private String generateConversationCode() {
        return "CHAT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private ChatConversationSummaryResponse toSummaryResponse(ChatConversation conversation) {
        return new ChatConversationSummaryResponse(
                conversation.getId(),
                conversation.getConversationCode(),
                conversation.getCustomerName(),
                conversation.getCustomerEmail(),
                conversation.getCustomerPhone(),
                conversation.getSubject(),
                conversation.getStatus(),
                conversation.getAssignedAdminName(),
                conversation.getLastMessageAt(),
                conversation.getLastMessagePreview(),
                conversation.getUnreadAdminCount(),
                conversation.getUnreadCustomerCount(),
                conversation.getCreatedAt(),
                conversation.getUpdatedAt());
    }

    private ChatConversationDetailResponse toDetailResponse(ChatConversation conversation) {
        return new ChatConversationDetailResponse(
                conversation.getId(),
                conversation.getConversationCode(),
                conversation.getCustomerName(),
                conversation.getCustomerEmail(),
                conversation.getCustomerPhone(),
                conversation.getSubject(),
                conversation.getStatus(),
                conversation.getAssignedAdminName(),
                conversation.getLastMessageAt(),
                conversation.getLastMessagePreview(),
                conversation.getUnreadAdminCount(),
                conversation.getUnreadCustomerCount(),
                conversation.getClosedAt(),
                conversation.getCreatedAt(),
                conversation.getUpdatedAt(),
                conversation.getMessages().stream()
                        .map(message -> new ChatMessageResponse(
                                message.getSenderType(),
                                message.getSenderName(),
                                message.getSenderEmail(),
                                message.getSenderPhone(),
                                message.getContent(),
                                message.getSentAt(),
                                message.getReadByAdminAt(),
                                message.getReadByCustomerAt()))
                        .toList());
    }
}

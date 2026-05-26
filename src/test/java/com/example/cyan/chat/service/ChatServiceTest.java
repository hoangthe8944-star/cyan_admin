package com.example.cyan.chat.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.cyan.chat.dto.CreateChatConversationRequest;
import com.example.cyan.chat.model.ChatConversation;
import com.example.cyan.chat.model.ChatMessage;
import com.example.cyan.chat.repository.ChatConversationRepository;
import com.example.cyan.common.exception.BadRequestException;
import com.example.cyan.common.model.enums.ChatConversationStatus;
import com.example.cyan.common.model.enums.ChatSenderType;
import com.example.cyan.common.model.enums.UserRole;
import com.example.cyan.user.model.User;
import com.example.cyan.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatConversationRepository chatConversationRepository;

    @Mock
    private UserRepository userRepository;

    private ChatService chatService;

    @BeforeEach
    void setUp() {
        chatService = new ChatService(chatConversationRepository, userRepository);
    }

    @Test
    void createConversationSeedsCustomerMessageAndUnreadAdminCount() {
        when(chatConversationRepository.save(any(ChatConversation.class))).thenAnswer(invocation -> {
            ChatConversation conversation = invocation.getArgument(0);
            conversation.setId("chat-1");
            conversation.setCreatedAt(Instant.now());
            conversation.setUpdatedAt(Instant.now());
            return conversation;
        });

        CreateChatConversationRequest request = new CreateChatConversationRequest();
        request.setCustomerName("Ngoc");
        request.setCustomerEmail("ngoc@example.com");
        request.setCustomerPhone("0123");
        request.setSubject("Hoi gia");
        request.setMessage("San pham nay con hang khong?");

        var response = chatService.createConversation(request);

        assertNotNull(response.id());
        assertNotNull(response.conversationCode());
        assertEquals(ChatConversationStatus.PENDING_ADMIN, response.status());
        assertEquals(1, response.unreadAdminCount());
        assertEquals(0, response.unreadCustomerCount());
        assertEquals(1, response.messages().size());
        assertEquals(ChatSenderType.CUSTOMER, response.messages().get(0).senderType());
    }

    @Test
    void createConversationForLoggedInUserLinksConversationToUser() {
        User user = new User();
        user.setId("user-1");
        user.setEmail("ngoc@example.com");
        user.setFullName("Ngoc The");
        user.setRole(UserRole.CUSTOMER);

        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(chatConversationRepository.save(any(ChatConversation.class))).thenAnswer(invocation -> {
            ChatConversation conversation = invocation.getArgument(0);
            conversation.setId("chat-2");
            conversation.setCreatedAt(Instant.now());
            conversation.setUpdatedAt(Instant.now());
            return conversation;
        });

        CreateChatConversationRequest request = new CreateChatConversationRequest();
        request.setCustomerUserId("user-1");
        request.setCustomerName("Ignored Name");
        request.setCustomerEmail("ignored@example.com");
        request.setCustomerPhone("0123");
        request.setSubject("Hoi bao hanh");
        request.setMessage("Cho minh hoi tinh trang don hang");

        var response = chatService.createConversation(request);

        assertEquals("user-1", response.customerUserId());
        assertEquals("Ngoc The", response.customerName());
        assertEquals("ngoc@example.com", response.customerEmail());
    }

    @Test
    void replyAsAdminMarksConversationAnsweredAndIncrementsUnreadCustomerCount() {
        ChatConversation conversation = new ChatConversation();
        conversation.setId("chat-1");
        conversation.setConversationCode("CHAT-12345678");
        conversation.setCustomerName("Ngoc");
        conversation.setStatus(ChatConversationStatus.PENDING_ADMIN);

        ChatMessage customerMessage = new ChatMessage();
        customerMessage.setSenderType(ChatSenderType.CUSTOMER);
        customerMessage.setSenderName("Ngoc");
        customerMessage.setContent("Cho minh hoi");
        customerMessage.setSentAt(Instant.now());
        conversation.getMessages().add(customerMessage);
        conversation.setUnreadAdminCount(1);
        conversation.setLastMessageAt(customerMessage.getSentAt());
        conversation.setLastMessagePreview(customerMessage.getContent());

        when(chatConversationRepository.findById("chat-1")).thenReturn(Optional.of(conversation));
        when(chatConversationRepository.save(any(ChatConversation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = chatService.replyAsAdmin("chat-1", "Admin A", "Ben minh van con hang");

        assertEquals(ChatConversationStatus.ANSWERED, response.status());
        assertEquals("Admin A", response.assignedAdminName());
        assertEquals(2, response.messages().size());
        assertEquals(1, response.unreadCustomerCount());
        assertEquals(ChatSenderType.ADMIN, response.messages().get(1).senderType());
    }

    @Test
    void addCustomerMessageRejectsClosedConversation() {
        ChatConversation conversation = new ChatConversation();
        conversation.setId("chat-1");
        conversation.setStatus(ChatConversationStatus.CLOSED);

        when(chatConversationRepository.findById("chat-1")).thenReturn(Optional.of(conversation));

        assertThrows(BadRequestException.class,
                () -> chatService.addCustomerMessage("chat-1", "Tin nhan moi"));
    }
}

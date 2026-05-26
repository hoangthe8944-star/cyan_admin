package com.example.cyan.chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.cyan.chat.model.ChatConversation;

public interface ChatConversationRepository extends MongoRepository<ChatConversation, String> {

    Optional<ChatConversation> findByConversationCode(String conversationCode);

    List<ChatConversation> findByCustomerUserId(String customerUserId, Sort sort);

    List<ChatConversation> findByCustomerEmailIgnoreCase(String customerEmail, Sort sort);
}

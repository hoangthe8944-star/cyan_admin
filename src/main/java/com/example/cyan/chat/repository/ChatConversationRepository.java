package com.example.cyan.chat.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.cyan.chat.model.ChatConversation;

public interface ChatConversationRepository extends MongoRepository<ChatConversation, String> {

    Optional<ChatConversation> findByConversationCode(String conversationCode);
}

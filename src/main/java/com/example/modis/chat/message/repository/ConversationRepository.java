package com.example.modis.chat.message.repository;

import com.example.modis.chat.message.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConversationRepository extends MongoRepository<Message, String> {
}

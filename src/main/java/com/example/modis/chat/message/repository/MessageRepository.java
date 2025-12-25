package com.example.modis.chat.message.repository;

import com.example.modis.chat.message.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {

    public Message findByMessageId(String messageId);

}

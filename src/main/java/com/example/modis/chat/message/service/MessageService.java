package com.example.modis.chat.message.service;

import com.example.modis.chat.message.dto.ListMessageDTO;
import com.example.modis.chat.message.dto.MessageDTO;
import com.example.modis.chat.message.model.Message;
import com.example.modis.chat.message.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ConvertOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Query.query;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final MessageRepository messageRepository;
    private final MongoTemplate mongoTemplate;
    public Message save(Message message) {
        return messageRepository.save(message);
    }

    public List<MessageDTO> getMessagesBetweenUserWithReceiver(String userId, String receiverId) {
        //right now, i want to extract all message between userId and receiverId
        Criteria criteria = new Criteria().orOperator(
                Criteria.where("participants.senderId").is(userId).and("participants.receiverId").is(receiverId),
                Criteria.where("participants.senderId").is(receiverId).and("participants.receiverId").is(userId)
        );
//        log.info("criteria:{}", criteria);
        Query query =  new Query(criteria)
                .with(Sort.by(Sort.Direction.ASC, "receiver.timestamp"));
//        log.info("query:{}", query);

        List<Message> messages = mongoTemplate.find(query, Message.class);

//        log.info("messages:{}", messages);

        // Convert List<Message> to List<MessageDTO>
        return messages.stream()
                .map(msg -> new MessageDTO(
                        msg.getMessageId(),
                        msg.getParticipants().getSenderId(),
                        msg.getParticipants().getReceiverId(),
                        msg.getReceiver().getContent(),
                        msg.getReceiver().getTimestamp() != null
                                ? msg.getReceiver().getTimestamp().toInstant()
                                : null
                ))
                .toList();
    }

}


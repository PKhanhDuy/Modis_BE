package com.example.modis.chat.message.service;

import com.example.modis.chat.message.dto.ListMessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ConvertOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.aggregation.ConditionalOperators.ifNull;
import static org.springframework.data.mongodb.core.aggregation.ConditionalOperators.when;

@Service
@RequiredArgsConstructor
public class ListMessService {
    private final MongoTemplate mongoTemplate;

    public List<ListMessageDTO> getRecentChatList(String currentUserId) {

        Aggregation aggregation = newAggregation(
                // --- BƯỚC 1: Tìm tin nhắn liên quan ---
                match(new Criteria().orOperator(
                        Criteria.where("participants.senderId").is(currentUserId),
                        Criteria.where("participants.receiverId").is(currentUserId)
                )),

                // --- BƯỚC 2: Sắp xếp mới nhất lên đầu ---
                sort(Sort.Direction.DESC, "timestamp"),

                // --- BƯỚC 3: Xác định ID đối phương và lấy thông tin message ---
                project()
                        .and("receiver.content").as("content")
                        .and("receiver.type").as("type")
                        .and("receiver.timestamp").as("timestamp")
                        .and(when(Criteria.where("participants.senderId").is(currentUserId))
                                .then("$participants.receiverId")
                                .otherwise("$participants.senderId"))
                        .as("partnerId"),

                // --- BƯỚC 4: Group theo partnerId để lấy tin nhắn cuối cùng ---
                group("partnerId")
                        .first("partnerId").as("partnerId")
                        .first("content").as("lastMessage")
                        .first("type").as("messageType")
                        .first("timestamp").as("timestamp"),

                // --- BƯỚC 5: Convert partnerId từ String sang ObjectId ---
                project("partnerId", "lastMessage", "messageType", "timestamp")
                        .and(ConvertOperators.ToObjectId.toObjectId("$partnerId")).as("partnerIdObj"),

                // --- BƯỚC 6: Lookup thông tin User ---
                lookup("users", "partnerIdObj", "_id", "partnerInfo"),

                // --- BƯỚC 7: Unwind để chuyển array thành object ---
                unwind("partnerInfo", true),

                // --- BƯỚC 8: Project cuối cùng (mapping sang DTO) ---
                project("lastMessage", "messageType", "timestamp")
                        .and("partnerId").as("partnerId")
                        .and("partnerId").as("conversationId")
                        .and(ifNull("$partnerInfo.fullname").then("Unknown User")).as("partnerName")
                        .and("$partnerInfo.avatarUrl").as("partnerAvatar"),

                // --- BƯỚC 9: Sort lại theo timestamp ---
                sort(Sort.Direction.DESC, "timestamp")
        );

        // Execute aggregation
        AggregationResults<ListMessageDTO> results = mongoTemplate.aggregate(
                aggregation, "messages", ListMessageDTO.class
        );

        return results.getMappedResults();
    }

}
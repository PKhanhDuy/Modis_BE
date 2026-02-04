package com.example.modis.friend.repository;

import com.example.modis.friend.model.FriendReq;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendReqRepository extends MongoRepository<FriendReq, String> {
    List<FriendReq> findByReceiverIdAndStatus(String receiverId, String status);
    List<FriendReq> findBySenderIdAndStatus(String senderId, String status);
    List<FriendReq> findByStatusAndSenderIdOrStatusAndReceiverId(
            String status1, String senderId,
            String status2, String receiverId
    );
    FriendReq findBySenderIdAndReceiverIdOrSenderIdAndReceiverId(
            String sender1, String receiver1,
            String sender2, String receiver2
    );
    boolean existsBySenderIdAndReceiverId(String senderId, String receiverId);
}

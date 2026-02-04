package com.example.modis.friend.service;

import com.example.modis.friend.dto.FriendReqResponse;
import com.example.modis.friend.dto.FriendResponse;
import com.example.modis.friend.model.FriendReq;
import com.example.modis.friend.repository.FriendReqRepository;
import com.example.modis.user.model.User;
import com.example.modis.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendReqService {
    private static final Logger log =
            LoggerFactory.getLogger(FriendReqService.class);
    private final FriendReqRepository friendReqRepository;
    private final UserRepository userRepository;

    public List<FriendResponse> getFriends(String userId) {
        List<FriendReq> list =
                friendReqRepository.findByStatusAndSenderIdOrStatusAndReceiverId(
                        "accepted", userId,
                        "accepted", userId
                );
        return list.stream().map(req -> {
            String friendId = req.getSenderId().equals(userId)
                    ? req.getReceiverId()
                    : req.getSenderId();
            User user = userRepository.findById(friendId)
                    .orElseThrow(() ->
                            new RuntimeException("User not found: " + friendId)
                    );
            return new FriendResponse(
                    req.getId(),                 // id của request
                    user.getId().toString(),     // id người bạn
                    user.getUsername(),          // username người bạn
                    user.getFullname()           // fullname người bạn
            );

        }).toList();
    }

    public List<FriendReqResponse> getReceivedRequestsWithUser(String userId) {
        List<FriendReq> list =
                friendReqRepository.findByReceiverIdAndStatus(userId, "pending");
        return list.stream().map(req -> {
            User sender = userRepository.findById(req.getSenderId())
                    .orElse(null);
            return new FriendReqResponse(
                    req.getId(),                         // id request
                    req.getSenderId(),                   // id người gửi
                    sender != null ? sender.getFullname() : null, // tên người gửi
                    req.getReceiverId(),                 // id người nhận
                    null,                                // receiverName không cần
                    req.getStatus(),                     // pending
                    req.getTimestamp()                   // thời gian gửi
            );
        }).toList();
    }

    public List<FriendReqResponse> getSentRequestsWithUser(String userId) {

        List<FriendReq> list =
                friendReqRepository.findBySenderIdAndStatus(userId, "pending");
        return list.stream().map(req -> {
            User receiver = userRepository.findById(req.getReceiverId())
                    .orElse(null);
            return new FriendReqResponse(
                    req.getId(),                         // id request
                    req.getSenderId(),                   // id người gửi
                    null,                                // senderName không cần
                    req.getReceiverId(),                 // id người nhận
                    receiver != null ? receiver.getFullname() : null, // tên người nhận
                    req.getStatus(),                     // pending
                    req.getTimestamp()                   // thời gian gửi
            );
        }).toList();
    }

    public FriendReq sendRequest(String senderId, String receiverId) {
        if (senderId.equals(receiverId)) {
            throw new RuntimeException("Cannot send friend request to yourself");
        }
        if (friendReqRepository.existsBySenderIdAndReceiverId(senderId, receiverId)
                || friendReqRepository.existsBySenderIdAndReceiverId(receiverId, senderId)) {
            throw new RuntimeException("Friend request already exists");
        }
        FriendReq friendReq = new FriendReq();
        friendReq.setSenderId(senderId);              // người gửi
        friendReq.setReceiverId(receiverId);          // người nhận
        friendReq.setStatus("pending");               // trạng thái ban đầu
        friendReq.setTimestamp(LocalDateTime.now());  // thời gian gửi
        return friendReqRepository.save(friendReq);
    }

    public FriendReq acceptRequest(String id, String userId) {
        log.info("Accept request id={} by userId={}", id, userId);
        FriendReq friendReq = friendReqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));
        if (!friendReq.getReceiverId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền chấp nhận lời mời này");
        }
        if (!"pending".equals(friendReq.getStatus())) {
            throw new RuntimeException("Lời mời đã được xử lý");
        }
        friendReq.setStatus("accepted");
        return friendReqRepository.save(friendReq);
    }

    public FriendReq rejectRequest(String id) {
        FriendReq friendReq = friendReqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));
        friendReq.setStatus("rejected");
        return friendReqRepository.save(friendReq);
    }

    public String getFriendStatus(String userId, String otherUserId) {
        FriendReq req = friendReqRepository
                .findBySenderIdAndReceiverIdOrSenderIdAndReceiverId(
                        userId, otherUserId,
                        otherUserId, userId
                );
        if (req == null) return "none";
        return req.getStatus();
    }

    public void deleteRequest(String id) {
        friendReqRepository.deleteById(id);
    }
}

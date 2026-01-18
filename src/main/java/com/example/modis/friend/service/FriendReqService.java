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

    // FRIEND LIST
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
                    req.getId(),
                    user.getId().toString(),
                    user.getUsername(),
                    user.getFullname()
            );

        }).toList();
    }

    // RECEIVED REQUESTS
    public List<FriendReqResponse> getReceivedRequestsWithUser(String userId) {

        List<FriendReq> list =
                friendReqRepository.findByReceiverIdAndStatus(userId, "pending");

        return list.stream().map(req -> {

            User sender = userRepository.findById(req.getSenderId())
                    .orElse(null);

            return new FriendReqResponse(
                    req.getId(),
                    req.getSenderId(),
                    sender != null ? sender.getFullname() : null,
                    req.getReceiverId(),
                    null,
                    req.getStatus(),
                    req.getTimestamp()
            );
        }).toList();
    }

    // SENT REQUESTS
    public List<FriendReqResponse> getSentRequestsWithUser(String userId) {

        List<FriendReq> list =
                friendReqRepository.findBySenderIdAndStatus(userId, "pending");

        return list.stream().map(req -> {

            User receiver = userRepository.findById(req.getReceiverId())
                    .orElse(null);

            return new FriendReqResponse(
                    req.getId(),
                    req.getSenderId(),
                    null,
                    req.getReceiverId(),
                    receiver != null ? receiver.getFullname() : null,
                    req.getStatus(),
                    req.getTimestamp()
            );
        }).toList();
    }

    // SEND REQUEST
    public FriendReq sendRequest(String senderId, String receiverId) {

        if (senderId.equals(receiverId)) {
            throw new RuntimeException("Cannot send friend request to yourself");
        }

        if (friendReqRepository.existsBySenderIdAndReceiverId(senderId, receiverId)
                || friendReqRepository.existsBySenderIdAndReceiverId(receiverId, senderId)) {
            throw new RuntimeException("Friend request already exists");
        }

        FriendReq friendReq = new FriendReq();
        friendReq.setSenderId(senderId);
        friendReq.setReceiverId(receiverId);
        friendReq.setStatus("pending");
        friendReq.setTimestamp(LocalDateTime.now());

        return friendReqRepository.save(friendReq);
    }

    // ACCEPT
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

    // REJECT
    public FriendReq rejectRequest(String id) {
        FriendReq friendReq = friendReqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));
        friendReq.setStatus("rejected");
        return friendReqRepository.save(friendReq);
    }

    // STATUS
    public String getFriendStatus(String userId, String otherUserId) {
        FriendReq req = friendReqRepository
                .findBySenderIdAndReceiverIdOrSenderIdAndReceiverId(
                        userId, otherUserId,
                        otherUserId, userId
                );

        if (req == null) return "none";
        return req.getStatus();
    }

    // DELETE
    public void deleteRequest(String id) {
        friendReqRepository.deleteById(id);
    }
}

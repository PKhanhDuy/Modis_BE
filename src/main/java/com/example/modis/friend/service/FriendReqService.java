package com.example.modis.friend.service;

import com.example.modis.friend.dto.FriendResponse;
import com.example.modis.friend.model.FriendReq;
import com.example.modis.friend.repository.FriendReqRepository;
import com.example.modis.user.model.User;
import com.example.modis.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
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

    // danh sách bạn bè
    public List<FriendResponse> getFriends(String userId) {

        List<FriendReq> list =
                friendReqRepository.findByStatusAndSenderIdOrStatusAndReceiverId(
                        "accepted", userId,
                        "accepted", userId
                );

        return list.stream().map(req -> {

            // xác định ID người kia
            String friendId = req.getSenderId().equals(userId)
                    ? req.getReceiverId()
                    : req.getSenderId();
            log.info("Da lay duoc friendId la " + friendId);
            User user = userRepository.findById(friendId)
                    .orElseThrow(() ->
                            new RuntimeException("User not found: " + friendId)
                    );

            return new FriendResponse(
                    req.getId(),
                    user.getId(),
                    user.getUsername(),
                    user.getFullname()
            );

        }).toList();
    }

    //  received friend requests
    public List<FriendReq> getReceivedRequests(String userId) {
        return friendReqRepository.findByReceiverIdAndStatus(userId, "pending");
    }

    // sent requests
    public List<FriendReq> getSentRequests(String userId) {
        return friendReqRepository.findBySenderIdAndStatus(userId, "pending");
    }

    // gui loi moi ket ban
    public FriendReq sendRequest(String senderId, String receiverId) {

        if (senderId.equals(receiverId)) {
            throw new RuntimeException("Cannot send friend request to yourself");
        }

        // chặn gửi trùng 2 chiều
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

    //    chap nhan loi moi ket ban
    public FriendReq acceptRequest(String id) {
        FriendReq friendReq = friendReqRepository.findById(id).orElseThrow(() -> new RuntimeException("Friend request not found"));
        friendReq.setStatus("accepted");
        return friendReqRepository.save(friendReq);
    }

    //    tu choi loi moi ket ban
    public FriendReq rejectRequest(String id) {
        FriendReq friendReq = friendReqRepository.findById(id).orElseThrow(() -> new RuntimeException("Friend request not found"));
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
        return req.getStatus(); // pending | accepted | rejected
    }

    //    huy loi moi ket ban
    public void deleteRequest(String id) {
        friendReqRepository.deleteById(id);
    }
}

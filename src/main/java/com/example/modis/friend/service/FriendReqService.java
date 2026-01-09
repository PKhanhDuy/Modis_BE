package com.example.modis.friend.service;

import com.example.modis.friend.model.FriendReq;
import com.example.modis.friend.repository.FriendReqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendReqService {
    private final FriendReqRepository friendReqRepository;

    // friendList
    public List<FriendReq> getFriends(String userId) {
        return friendReqRepository.findByStatusAndSenderIdOrStatusAndReceiverId("accepted", userId, "accepted", userId);
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

    //    huy loi moi ket ban
    public void deleteRequest(String id) {
        friendReqRepository.deleteById(id);
    }
}

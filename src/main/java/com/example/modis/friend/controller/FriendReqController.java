package com.example.modis.friend.controller;

import com.example.modis.friend.dto.FriendResponse;
import com.example.modis.friend.model.FriendReq;
import com.example.modis.friend.service.FriendReqService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendReqController {
    private final FriendReqService friendReqService;

    //    danh sach ban be
    @GetMapping("/list")
    public List<FriendResponse> friends(@RequestParam String userId) {
        return friendReqService.getFriends(userId);
    }

    //    received request
    @GetMapping("/requests/received")
    public List<FriendReq> requestsReceived(@RequestParam String userId) {
        return friendReqService.getReceivedRequests(userId);
    }

    //     sent request
    @GetMapping("/requests/sent")
    public List<FriendReq> requestsSent(@RequestParam String userId) {
        return friendReqService.getSentRequests(userId);
    }

    //    gui loi moi ket ban
    @PostMapping("/request")
    public FriendReq sendRequest(@RequestParam String senderId, @RequestParam String receiverId) {
        return friendReqService.sendRequest(senderId, receiverId);
    }

    // chap nhan loi moi ket ban
    @PutMapping("/request/{id}/accept")
    public FriendReq acceptRequest(@PathVariable String id) {
        return friendReqService.acceptRequest(id);
    }

    //    tu choi loi moi ket ban
    @PutMapping("/request/{id}/reject")
    public FriendReq rejectRequest(@PathVariable String id) {
        return friendReqService.rejectRequest(id);
    }

    // huy loi moi ket ban
    @DeleteMapping("/request/{id}")
    public void deleteRequest(@PathVariable String id) {
        friendReqService.deleteRequest(id);
    }
}

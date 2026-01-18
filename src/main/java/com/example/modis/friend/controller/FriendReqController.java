package com.example.modis.friend.controller;

import com.example.modis.friend.dto.FriendResponse;
import com.example.modis.friend.model.FriendReq;
import com.example.modis.friend.service.FriendReqService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendReqController {

    private final FriendReqService friendReqService;

    private void log(String msg) {
        System.out.println(" [FriendController] " + msg);
    }

    // danh sach ban be
    @GetMapping("/list")
    public List<FriendResponse> friends(@RequestParam String userId) {
        log("GET /list userId=" + userId);
        return friendReqService.getFriends(userId);
    }

    // received request
    @GetMapping("/requests/received")
    public List<FriendReq> requestsReceived(@RequestParam String userId) {
        return friendReqService.getReceivedRequests(userId);
    }

    // sent request
    @GetMapping("/requests/sent")
    public List<FriendReq> requestsSent(@RequestParam String userId) {
        log("GET /requests/sent userId=" + userId);
        return friendReqService.getSentRequests(userId);
    }

    // gui loi moi ket ban
    @PostMapping("/request")
    public FriendReq sendRequest(@RequestParam String senderId,
                                 @RequestParam String receiverId) {
        log("POST /request senderId=" + senderId + " receiverId=" + receiverId);
        return friendReqService.sendRequest(senderId, receiverId);
    }

    //chap nhan
    @PutMapping("/request/{id}/accept")
    public FriendReq acceptRequest(@PathVariable String id) {
        String userId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return friendReqService.acceptRequest(id, userId);
    }

    // tu choi loi moi ket ban
    @PutMapping("/request/{id}/reject")
    public FriendReq rejectRequest(@PathVariable String id) {
        log("PUT /request/" + id + "/reject");
        return friendReqService.rejectRequest(id);
    }

    @GetMapping("/status")
    public ResponseEntity<?> getFriendStatus(@RequestParam String userId,
                                             @RequestParam String otherUserId) {
        log("GET /status userId=" + userId + " otherUserId=" + otherUserId);
        return ResponseEntity.ok(
                Map.of(
                        "status", "success",
                        "data", friendReqService.getFriendStatus(userId, otherUserId)
                )
        );
    }

    // xoa ket ban
    @DeleteMapping("/request/{id}")
    public void deleteRequest(@PathVariable String id) {
        log("DELETE /request/" + id);
        friendReqService.deleteRequest(id);
    }
}

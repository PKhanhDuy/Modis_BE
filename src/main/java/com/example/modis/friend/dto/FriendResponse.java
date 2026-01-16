package com.example.modis.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FriendResponse {
    private String friendReqId;
    private String userId;
    private String username;
    private String fullname;
}

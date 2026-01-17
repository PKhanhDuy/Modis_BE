package com.example.modis.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
@AllArgsConstructor
public class FriendResponse {
    private String friendReqId;
    private ObjectId userId;
    private String username;
    private String fullname;
}

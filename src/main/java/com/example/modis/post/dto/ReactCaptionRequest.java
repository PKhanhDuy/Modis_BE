package com.example.modis.post.dto;

import com.example.modis.post.model.Receiver;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReactCaptionRequest {
    private String postId;
    private String senderId;
    private String reaction;
}
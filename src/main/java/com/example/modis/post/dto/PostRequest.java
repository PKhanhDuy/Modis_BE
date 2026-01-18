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
public class PostRequest {
    private String senderId;
    private List<Receiver> receivers;
    private String caption;
    private String urlImage;
}
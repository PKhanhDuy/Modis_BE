package com.example.modis.post.dto;
import com.example.modis.post.model.Receiver;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private String id;
    private String senderId;
    private Receiver receiver;
    private String caption;
    private String urlImage;
    private Instant created_at;
}

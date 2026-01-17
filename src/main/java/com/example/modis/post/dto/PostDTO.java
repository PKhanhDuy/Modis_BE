package com.example.modis.post.dto;

import com.example.modis.post.model.Receiver;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class PostDTO {
    private String id;
    private String senderId;
    private Receiver receiver;
    private String caption;
    private String urlImage;
    private Instant created_at;

    public PostDTO(String id, String senderId, Receiver receiver, String caption, String urlImage, Instant created_at) {
        this.id = id;
        this.senderId = senderId;
        this.receiver = receiver;
        this.caption = caption;
        this.urlImage = urlImage;
        this.created_at = created_at;
    }
    public PostDTO(){};
}

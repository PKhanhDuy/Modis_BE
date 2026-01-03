package com.example.modis.post.dto;

import com.example.modis.post.model.Receiver;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class PostDTO {
    private String senderId;
    private Receiver receiver;
    private String caption;
    private String urlImage;
    private Date created_at;

    public PostDTO(String senderId, Receiver receiver, String caption, String urlImage, Date created_at) {
        this.senderId = senderId;
        this.receiver = receiver;
        this.caption = caption;
        this.urlImage = urlImage;
        this.created_at = created_at;
    }
    public PostDTO(){}
}

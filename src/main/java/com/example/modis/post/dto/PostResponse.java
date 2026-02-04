package com.example.modis.post.dto;
import com.example.modis.post.model.Receiver;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    @Id
    @JsonProperty("_id")
    private String id;
    private String senderId;
    private String senderName;
    private String senderAvatar;
    private List<ReceiverDTO> receivers;
    private String caption;
    private String urlImage;
    private Instant created_at;
}

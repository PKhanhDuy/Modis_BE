package com.example.modis.post.dto;
import com.example.modis.post.model.Receiver;
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
    private String id;
    private String senderId;
    private List<Receiver> receivers;
    private String caption;
    private String urlImage;
    private Instant created_at;
}

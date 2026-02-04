package com.example.modis.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;


import java.time.Instant;
import java.util.Date;

@Data
@Builder
public class PostSimpleDTO {
    @JsonProperty("_id")
    private String id;
    private String urlImage;
    private Instant created_at;

    public PostSimpleDTO(String id, String urlImage, Instant created_at){
        this.id = id;
        this.urlImage = urlImage;
        this.created_at = created_at;
    }
    public PostSimpleDTO(){};
}

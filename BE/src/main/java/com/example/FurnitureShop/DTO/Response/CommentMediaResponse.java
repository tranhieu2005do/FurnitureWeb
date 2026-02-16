package com.example.FurnitureShop.DTO.Response;

import com.example.FurnitureShop.Model.CommentMedia;
import com.example.FurnitureShop.Model.CommentMedia.MediaType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentMediaResponse {

    @JsonProperty("url")
    private String url;

    @JsonProperty("thumbnail")
    private String thumbnail;

    @JsonProperty("media_type")
    private MediaType mediaType;

    @JsonProperty("duration")
    private Double durationSeconds;

    private Integer position;

    public static CommentMediaResponse fromEntity(CommentMedia commentMedia) {
        return CommentMediaResponse.builder()
                .mediaType(commentMedia.getMediaType())
                .durationSeconds(commentMedia.getDuration())
                .url(commentMedia.getUrl())
                .thumbnail(commentMedia.getThumbnail())
                .position(commentMedia.getPosition())
                .build();
    }
}

package com.example.FurnitureShop.DTO.Request;

import com.example.FurnitureShop.Model.CommentMedia.MediaType;
import lombok.Data;

import java.util.List;

@Data
public class CreatedCommentRequest {

    private Long productId;
    private String content;
    private List<MediaRequest> medias;

    @Data
    public static class MediaRequest{
        private String url;
        private String thumbnail;
        private MediaType mediaType;
        private Integer duration;
        private Integer position;
    }
}

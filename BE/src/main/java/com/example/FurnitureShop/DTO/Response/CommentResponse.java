package com.example.FurnitureShop.DTO.Response;

import com.example.FurnitureShop.Model.Comment;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CommentResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("media_response")
    private List<CommentMediaResponse> commentMedia;
}

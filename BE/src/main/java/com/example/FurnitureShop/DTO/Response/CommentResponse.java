package com.example.FurnitureShop.DTO.Response;

import com.example.FurnitureShop.Model.Comment;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentResponse {

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("user_name")
    private String userName;

    public static CommentResponse fromEntity(Comment comment){
        return CommentResponse.builder()
                .comment(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .userName(comment.getUser().getFullName())
                .build();
    }
}

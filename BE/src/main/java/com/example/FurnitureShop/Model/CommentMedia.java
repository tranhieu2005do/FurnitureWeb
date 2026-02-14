package com.example.FurnitureShop.Model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;


@Entity
@Table(name = "comment_media")
@Data
@Builder
@RequiredArgsConstructor
public class CommentMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "comment_id")
    private Long commentId;

    @Column(name = "url")
    private String url;

    @Column(name = "thumbnail_url")
    private String thumbnail;

    @Column(name = "media_type")
    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    @Column(name = "duration_seconds")
    private Integer duration;

    @Column(name = "position")
    private Integer position;

    public static enum MediaType{
        IMAGE,VIDEO
    }
}

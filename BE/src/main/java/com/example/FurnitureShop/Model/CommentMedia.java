package com.example.FurnitureShop.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "comment_media")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private Double duration;

    @Column(name = "position")
    private Integer position;

    public static enum MediaType{
        image,video
    }
}

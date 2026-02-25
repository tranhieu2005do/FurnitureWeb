package com.example.FurnitureShop.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "message_media")
@Builder
@Data @AllArgsConstructor @NoArgsConstructor
public class MessageMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    @JsonIgnore
    private Message message;

    @Column(name = "url")
    private String url;

    @Transient
    private String fileName;

    @Transient
    private Long fileSize;

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private Type type;

    public enum Type{
        IMAGE,VIDEO,FILE
    }
}

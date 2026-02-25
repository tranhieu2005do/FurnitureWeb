package com.example.FurnitureShop.DTO.Response;

import com.example.FurnitureShop.Model.MessageMedia;
import com.example.FurnitureShop.Model.MessageMedia.Type;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor @AllArgsConstructor
public class MessageMediaResponse {

    @JsonProperty("fileName")
    private String fileName;
    @JsonProperty("fileSize")
    private Long fileSize;
    private String url;
    private String thumbnail;
    private Type type;

    public static MessageMediaResponse fromEntity(MessageMedia message){
        return MessageMediaResponse.builder()
                .url(message.getUrl())
                .fileName(message.getFileName())
                .fileSize(message.getFileSize())
                .type(message.getType())
                .thumbnail(message.getThumbnail())
                .build();
    }
}

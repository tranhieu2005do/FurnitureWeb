package com.example.FurnitureShop.DTO.Response;

import com.example.FurnitureShop.Model.Message;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class MessageResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("senderName")
    private String senderName;

    @JsonProperty("senderId")
    private Long senderId;

    @JsonProperty("message")
    private String message;

    @JsonProperty("sentAt")
    private LocalDateTime sentAt;
    private List<MessageMediaResponse> medias;

    public static MessageResponse fromEntity(Message message){
        return MessageResponse.builder()
                .message(message.getContent())
                .id(message.getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getFullName())
                .sentAt(message.getCreatedAt())
                .medias(message.getMedias().stream().map(MessageMediaResponse::fromEntity).toList())
                .build();
    }
}

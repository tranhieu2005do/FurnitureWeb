package com.example.FurnitureShop.DTO.Request;

import com.example.FurnitureShop.Model.Message;
import com.example.FurnitureShop.Model.Message.MessageType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ChatMessage {
    @JsonProperty("conservation_id")
    private Long conservationId;

    @JsonProperty("sender_id")
    private Long senderId;

    private String sender;

    @JsonProperty("receiver_id")
    private Long receiverId;

    @JsonProperty("content")
    private String content;

    @JsonProperty("type")
    private MessageType messageType;

    public static ChatMessage fromMessage(Message message) {
        return ChatMessage.builder()
                .conservationId(message.getConservation().getId())
                .sender(message.getSender().getFullName())
                .receiverId(1L)
                .messageType(message.getType())
                .content(message.getContent())
                .build();
    }
}

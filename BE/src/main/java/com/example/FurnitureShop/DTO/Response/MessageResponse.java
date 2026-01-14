package com.example.FurnitureShop.DTO.Response;

import com.example.FurnitureShop.Model.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class MessageResponse {
    private String senderName;
    private String message;

    public static MessageResponse fromEntity(Message message){
        return MessageResponse.builder()
                .message(message.getContent())
                .senderName(message.getSender().getFullName())
                .build();
    }
}

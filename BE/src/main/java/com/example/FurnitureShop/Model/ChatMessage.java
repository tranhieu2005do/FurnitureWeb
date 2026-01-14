package com.example.FurnitureShop.Model;

import lombok.Data;

@Data
public class ChatMessage {

    private String sender;
    private String message;
    private MessageType type;

    public enum MessageType {
        CHAT, JOIN, LEAVE
    }
}

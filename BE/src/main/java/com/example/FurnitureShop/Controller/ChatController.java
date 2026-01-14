package com.example.FurnitureShop.Controller;

import com.example.FurnitureShop.DTO.Request.ChatMessage;
import com.example.FurnitureShop.Service.Implement.ChatService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@AllArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
        log.info("{} is sending message: {}", message.getSender(), message.getContent());

        chatService.saveMessage(message);
        // Kiểm tra null trước khi set
        if (message.getSender() != null) {
            headerAccessor.getSessionAttributes().put("username", message.getSender());
        }

        return message;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage message, SimpMessageHeaderAccessor headerAccessor){
        log.info("User joined: {}", message.getSender());

        if (message.getSender() != null) {
            headerAccessor.getSessionAttributes().put("username", message.getSender());
        }

        return message;
    }
}

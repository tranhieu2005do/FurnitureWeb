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
@AllArgsConstructor
@Controller
public class ChatController {
    private final ChatService chatService;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public void sendMessage(
            @Payload ChatMessage message,
            SimpMessageHeaderAccessor headerAccessor
    ){}

}

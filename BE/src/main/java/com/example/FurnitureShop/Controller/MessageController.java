package com.example.FurnitureShop.Controller;

import com.example.FurnitureShop.DTO.Response.ApiResponse;
import com.example.FurnitureShop.DTO.Response.MessageResponse;
import com.example.FurnitureShop.DTO.Response.PageResponse;
import com.example.FurnitureShop.Model.CustomUserDetails;
import com.example.FurnitureShop.Service.Implement.ChatService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/conservation")
public class MessageController {
    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> sendMessage(
            @RequestParam Long conservationId,
            @RequestParam String content,
            @RequestParam Long senderId,
            @RequestParam(required = false) List<MultipartFile> files
    ) throws IOException {
        chatService.sendMessage(conservationId, content, senderId, files);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Send successfully!")
                .build());
    }

    @GetMapping("/messages")
    public ResponseEntity<ApiResponse<PageResponse<MessageResponse>>> loadMessages(
            @RequestParam Long conservationId,
            @RequestParam(required = false) Long lastMessageId,
            Pageable pageable
    ) throws IOException {
        return ResponseEntity.ok(ApiResponse.<PageResponse<MessageResponse>>builder()
                .message("Load messages successfully!")
                .statusCode(HttpStatus.OK.value())
                .data(chatService.loadMessages(conservationId, lastMessageId, pageable))
                .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Long>> getConversation(){
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Long customerId = userDetails.getUserId();
        return ResponseEntity.ok(ApiResponse.<Long>builder()
                .data(chatService.getOrCreateConversation(customerId))
                .message("Get conversation successfully!")
                .statusCode(HttpStatus.OK.value())
                .build());
    }
}

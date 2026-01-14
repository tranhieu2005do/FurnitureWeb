package com.example.FurnitureShop.Controller;

import com.example.FurnitureShop.DTO.Response.ApiResponse;
import com.example.FurnitureShop.DTO.Response.ConservationResponse;
import com.example.FurnitureShop.Service.Implement.ChatService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/conservation")
public class MessageController {
    private final ChatService chatService;

    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @userService.isSelf(#userId))")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<ConservationResponse>> getByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.<ConservationResponse>builder()
                .data(chatService.getOrCreateConservationByUserId(userId))
                .message("Get conservation successfully!")
                .statusCode(HttpStatus.OK.value())
                .build());
    }
}

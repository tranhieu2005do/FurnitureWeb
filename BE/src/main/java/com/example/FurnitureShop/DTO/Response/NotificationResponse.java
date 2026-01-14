package com.example.FurnitureShop.DTO.Response;

import com.example.FurnitureShop.Model.Notification;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class NotificationResponse {
    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("topic")
    private String topic;

    @JsonProperty("message")
    private String message;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    public static NotificationResponse fromEntity(Notification notification) {
        return NotificationResponse.builder()
                .userName(notification.getUser().getFullName())
                .topic(notification.getTopic())
                .message(notification.getMessage())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}

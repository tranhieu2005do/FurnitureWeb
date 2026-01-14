package com.example.FurnitureShop.DTO.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class NotificationRequest {
    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("topic")
    private String topic;

    @JsonProperty("message")
    private String message;

}

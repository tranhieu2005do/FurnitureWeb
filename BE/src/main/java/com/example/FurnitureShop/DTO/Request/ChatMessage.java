package com.example.FurnitureShop.DTO.Request;

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

    @JsonProperty("content")
    private String content;
}

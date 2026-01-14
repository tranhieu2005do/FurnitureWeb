package com.example.FurnitureShop.DTO.Request;

import com.example.FurnitureShop.DTO.Request.Kafka.OrderItemDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCreatedMailRequest {

//    @JsonProperty("track_number")
//    private String trackNumber;
    @JsonProperty("to")
    private String to;
    @JsonProperty("content")
    private String content;
    @JsonProperty("subject")
    private String subject;
    @JsonProperty("props")
    private Map<String, Object> props;
//    private String fullName;
//    private BigDecimal bill;
//    private Integer discount;
//    private BigDecimal totalAmount;
    @JsonProperty("notificationRequest")
    private NotificationRequest notificationRequest;
    @JsonProperty("orderItems")
    private List<OrderItemDTO> orderItems;
}

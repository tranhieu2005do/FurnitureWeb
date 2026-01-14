package com.example.FurnitureShop.DTO.Request;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MailRequest {
    private String to;
    private String subject;
    private String content;
    private Map<String,Object> props = new HashMap<>();
}

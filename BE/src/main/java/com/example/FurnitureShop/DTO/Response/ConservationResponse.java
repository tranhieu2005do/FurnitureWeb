package com.example.FurnitureShop.DTO.Response;

import com.example.FurnitureShop.Model.Conservation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ConservationResponse {

    private String userName;
    private String staffName;
    private List<MessageResponse> messages;

    public static ConservationResponse fromEntity(Conservation conservation) {
        return ConservationResponse.builder()
                .userName(conservation.getCustomer().getFullName())
                .staffName(conservation.getStaff().getFullName())
                .messages(conservation.getMessages().stream().map(MessageResponse::fromEntity).toList())
                .build();
    }
}

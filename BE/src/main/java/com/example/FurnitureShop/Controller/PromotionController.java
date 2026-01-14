package com.example.FurnitureShop.Controller;

import com.example.FurnitureShop.DTO.Request.PromotionRequest;
import com.example.FurnitureShop.DTO.Response.ApiResponse;
import com.example.FurnitureShop.DTO.Response.PromotionResponse;
import com.example.FurnitureShop.Service.Implement.PromotionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/promotions")
public class PromotionController {
    private final PromotionService promotionService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @PostMapping
    public ResponseEntity<ApiResponse<PromotionResponse>> create(@RequestBody @Valid PromotionRequest promotionRequest) {
        return ResponseEntity.ok(ApiResponse.<PromotionResponse>builder()
                .message("Promotion Created")
                .statusCode(HttpStatus.CREATED.value())
                .data(promotionService.create(promotionRequest))
                .build());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<PromotionResponse>>> getPromotions(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) boolean active){
        return ResponseEntity.ok(ApiResponse.<List<PromotionResponse>>builder()
                .data(promotionService.filterByProduct(productId, active))
                .statusCode(HttpStatus.OK.value())
                .message("Promotion List")
                .build());
    }

//    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
//    @PatchMapping("/{promotionId}")
//    public ResponseEntity<ApiResponse<Void>> inActivePromotion(@PathVariable Long promotionId) {
//        promotionService.inActivePromotion(promotionId);
//        return ResponseEntity.ok(ApiResponse.<Void>builder()
//                .message("Promotion In Active")
//                .statusCode(HttpStatus.OK.value())
//                .build());
//    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @GetMapping("/code")
    public ResponseEntity<ApiResponse<PromotionResponse>> findByCode(@RequestParam  String code ) {
        return  ResponseEntity.ok(ApiResponse.<PromotionResponse>builder()
                .message("Promotion Found")
                .statusCode(HttpStatus.OK.value())
                .data(promotionService.findByCode(code))
                .build());
    }
}

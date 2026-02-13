package com.example.FurnitureShop.Controller;

import com.example.FurnitureShop.DTO.Request.RatingRequest;
import com.example.FurnitureShop.DTO.Response.ApiResponse;
import com.example.FurnitureShop.DTO.Response.PageResponse;
import com.example.FurnitureShop.DTO.Response.ProductRatingAvgResponse;
import com.example.FurnitureShop.DTO.Response.RatingResponse;
import com.example.FurnitureShop.Service.Implement.RateService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/rates")
public class RateController {

    private final RateService rateService;

    @PostMapping
    public ResponseEntity<ApiResponse<RatingResponse>> create(@RequestBody @Valid RatingRequest request){
        return ResponseEntity.ok(ApiResponse.<RatingResponse>builder()
                .data(rateService.createrating(request))
                .message("Rating created")
                .statusCode(HttpStatus.CREATED.value())
                .build());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<List<RatingResponse>>> getRateOfProduct(
            @PathVariable Long productId,
            @RequestParam(required = false) Integer star,
            @RequestParam(required = false) boolean comments)
    {
        return ResponseEntity.ok(ApiResponse.<List<RatingResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Rating getting")
                .data(rateService.getRateOfProduct(productId, star, comments))
                .build());
    }

    @DeleteMapping("/{ratingId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long ratingId) {
        rateService.deleteRating(ratingId);
        return  ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Rating deleted")
                .statusCode(HttpStatus.NO_CONTENT.value())
                .build());
    }
}

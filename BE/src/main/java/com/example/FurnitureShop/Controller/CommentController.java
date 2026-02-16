package com.example.FurnitureShop.Controller;

import com.cloudinary.Api;
import com.example.FurnitureShop.DTO.Response.ApiResponse;
import com.example.FurnitureShop.DTO.Response.CommentResponse;
import com.example.FurnitureShop.DTO.Response.PageResponse;
import com.example.FurnitureShop.Model.CustomUserDetails;
import com.example.FurnitureShop.Service.Implement.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ApiResponse<CommentResponse>> sendComment(
            @RequestParam("productId") Long productId,
            @RequestParam("content") String content,
            @RequestParam(required = false) List<MultipartFile> files
    ) throws IOException {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        Long userId = userDetails.getUserId();
        return ResponseEntity.ok(ApiResponse.<CommentResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("success")
                .data(commentService.createComment(productId,content,userId,files))
                .build());
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<ApiResponse<PageResponse<CommentResponse>>> getCommentsOfProduct(
            @PathVariable("id") Long productId,
            Pageable pageable
    ){
        return ResponseEntity.ok(ApiResponse.<PageResponse<CommentResponse>>builder()
                .data(commentService.getCommentOfProductByProductId(productId,pageable))
                .message("success")
                .statusCode(HttpStatus.OK.value())
                .build());
    }
}

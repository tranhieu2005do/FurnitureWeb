package com.example.FurnitureShop.Service.Implement;

import com.example.FurnitureShop.DTO.Request.CreatedCommentRequest;
import com.example.FurnitureShop.DTO.Request.CreatedCommentRequest.MediaRequest;
import com.example.FurnitureShop.DTO.Response.CommentResponse;
import com.example.FurnitureShop.Model.Comment;
import com.example.FurnitureShop.Model.CommentMedia;
import com.example.FurnitureShop.Model.Product;
import com.example.FurnitureShop.Model.User;
import com.example.FurnitureShop.Repository.CommentMediaRepository;
import com.example.FurnitureShop.Repository.CommentRepository;
import com.example.FurnitureShop.Repository.ProductRepository;
import com.example.FurnitureShop.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMediaRepository commentMediaRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @CacheEvict(allEntries = true)
    public CommentResponse createComment(CreatedCommentRequest request, Long userId){
        User user =  userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Comment newComment = Comment.builder()
                .createdAt(LocalDateTime.now())
                .content(request.getContent())
                .user(user)
                .product(product)
                .build();

        commentRepository.save(newComment);

        if(request.getMedias().size() > 0){
            for(MediaRequest mediaRequest : request.getMedias()){
                CommentMedia media = CommentMedia.builder()
                        .commentId(newComment.getId())
                        .url(mediaRequest.getUrl())
                        .duration(mediaRequest.getDuration())
                        .position(mediaRequest.getPosition())
                        .thumbnail(mediaRequest.getThumbnail())
                        .mediaType(mediaRequest.getMediaType())
                        .build();
                commentMediaRepository.save(media);
            }
        }
        return CommentResponse.fromEntity(newComment);
    }
}

package com.example.FurnitureShop.Service.Implement;

import com.example.FurnitureShop.DTO.Request.NotificationRequest;
import com.example.FurnitureShop.DTO.Response.CommentMediaResponse;
import com.example.FurnitureShop.DTO.Response.CommentResponse;
import com.example.FurnitureShop.DTO.Response.PageResponse;
import com.example.FurnitureShop.DTO.Response.UploadResult;
import com.example.FurnitureShop.Model.Comment;
import com.example.FurnitureShop.Model.CommentMedia;
import com.example.FurnitureShop.Model.CommentMedia.MediaType;
import com.example.FurnitureShop.Model.Product;
import com.example.FurnitureShop.Model.User;
import com.example.FurnitureShop.Repository.CommentMediaRepository;
import com.example.FurnitureShop.Repository.CommentRepository;
import com.example.FurnitureShop.Repository.ProductRepository;
import com.example.FurnitureShop.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@CacheConfig(cacheNames = "comments")
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMediaRepository commentMediaRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CloudinaryService cloudinaryService;
    private final NotificationService notificationService;

    @CacheEvict(allEntries = true)
    public CommentResponse createComment(
            Long productId,
            String content,
            Long userId,
            List<MultipartFile> files
    ) throws IOException {
        User user =  userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Comment newComment = Comment.builder()
                .createdAt(LocalDateTime.now())
                .content(content)
                .user(user)
                .product(product)
                .build();

        commentRepository.save(newComment);

        CommentResponse response = CommentResponse.builder()
                .id(newComment.getId())
                .comment(newComment.getContent())
                .userName(user.getFullName())
                .createdAt(LocalDateTime.now())
                .build();

        List<CommentMediaResponse> mediaResponses = new ArrayList<>();
        Integer positon = 1;
        String folder = "furniture-web/comments";
        if(files != null && !files.isEmpty()){
            for(MultipartFile file : files){
                UploadResult result = cloudinaryService.upload(file, folder);
                log.info("Upload result {} is {}", positon, result);
                CommentMedia newCommentMedia = CommentMedia.builder()
                        .commentId(newComment.getId())
                        .url(result.getUrl())
                        .thumbnail(result.getThumbnailUrl())
                        .duration(result.getDuration())
                        .mediaType(result.getResourceType().equals("video") ? MediaType.video : MediaType.image)
                        .position(positon)
                        .build();
                positon++;
                commentMediaRepository.save(newCommentMedia);
                CommentMediaResponse mediaResponse = CommentMediaResponse.fromEntity(newCommentMedia);
                mediaResponses.add(mediaResponse);
            }
        }
        response.setCommentMedia(mediaResponses);

        // Tạo thông báo có comemnts tới cho các tài khoản role là staff
        List<User> staffs = userRepository.findByRoleId(2L);
        String message = "Khách hàng" + user.getFullName() + " vừa để lại nhận xét vào sản phẩm " + product.getName();
        String topic = "Có comment mới từ khách hàng";
        for(User u : staffs){
            NotificationRequest request = NotificationRequest.builder()
                    .topic(topic)
                    .message(message)
                    .userId(u.getId())
                    .build();
            notificationService.create(request);
        }
        return response;
    }

    @Transactional(readOnly = true)
    @Cacheable(key = "#commentId")
    public List<CommentMediaResponse> getMediaByCommentId(Long commenyId){
        return commentMediaRepository
                .getCommentMediaByCommentId(commenyId)
                .stream()
                .map(CommentMediaResponse::fromEntity)
                .toList();

    }

    private CommentResponse map(Comment comment){
        CommentResponse response = CommentResponse.builder()
                .id(comment.getId())
                .comment(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .userName(comment.getUser().getFullName())
                .build();

        List<CommentMediaResponse> mediaResponses = this.getMediaByCommentId(comment.getId());
        response.setCommentMedia(mediaResponses);
        return response;
    }

    @Transactional(readOnly = true)
    @Cacheable(key = "T(java.util.Objects).hash(#pageable.pageNumber, #pageable.pageSize, #productId)")
    public PageResponse<CommentResponse> getCommentOfProductByProductId(
            Long productId,
            Pageable pageable
    ){
        return PageResponse.fromPage(
                commentRepository.getCommentsByProductId(productId, pageable)
                        .map(this::map));
    }
}

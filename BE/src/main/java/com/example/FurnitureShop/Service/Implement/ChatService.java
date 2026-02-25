package com.example.FurnitureShop.Service.Implement;

import com.example.FurnitureShop.DTO.Response.MessageResponse;
import com.example.FurnitureShop.DTO.Response.PageResponse;
import com.example.FurnitureShop.DTO.Response.UploadResult;
import com.example.FurnitureShop.Exception.NotFoundException;
import com.example.FurnitureShop.Model.Conservation;
import com.example.FurnitureShop.Model.Message;
import com.example.FurnitureShop.Model.MessageMedia;
import com.example.FurnitureShop.Model.User;
import com.example.FurnitureShop.Repository.ConservationRepository;
import com.example.FurnitureShop.Repository.MessageMediaRepository;
import com.example.FurnitureShop.Repository.MessageRepository;
import com.example.FurnitureShop.Repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class ChatService {

    private final ConservationRepository conservationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final MessageMediaRepository messageMediaRepository;

    @Transactional
    public void sendMessage(
            Long conservationId,
            String content,
            Long senderId,
            List<MultipartFile> files
    ) throws IOException {
        Conservation conservation = conservationRepository.findById(conservationId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy cuộc trò chuyện."));
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thông tin người dùng này."));
        Message newMessage = Message.builder()
                .content(content)
                .sender(sender)
                .conservation(conservation)
                .createdAt(LocalDateTime.now())
                .build();
        messageRepository.save(newMessage);
        String folder = "furniture-web/chat";
        if(files != null && !files.isEmpty()){
            for(MultipartFile file : files){
                UploadResult result = cloudinaryService.upload(file, folder);
                String type = result.getResourceType();
                MessageMedia media = MessageMedia.builder()
                        .message(newMessage)
                        .fileName(result.getOriginalFileName())
                        .fileSize(result.getSize())
                        .url(result.getUrl())
                        .thumbnail(result.getThumbnailUrl())
                        .build();
                if(type.equals("image")){
                    media.setType(MessageMedia.Type.IMAGE);
                }
                else if(type.equals("video")){
                    media.setType(MessageMedia.Type.VIDEO);
                }
                else{
                    media.setType(MessageMedia.Type.FILE);
                }
                messageMediaRepository.save(media);
            }
        }
    }

//    @Transactional(readOnly = true)
    public Long getOrCreateConversation(Long customerId){
        Conservation conservation = conservationRepository.getConservationByUserId(customerId);
        if(conservation == null){
            User customer = userRepository.findById(customerId)
                    .orElseThrow(() -> new NotFoundException("User not found"));
            Conservation newConversation = Conservation.builder()
                    .customer(customer)
                    .createdAt(LocalDateTime.now())
                    .build();
            conservation = conservationRepository.save(newConversation);
        }
        return conservation.getId();
    }

    @Transactional(readOnly = true)
    public PageResponse<MessageResponse> loadMessages(Long conversationId, Long lastMessageId, Pageable pageable){
        return PageResponse.fromPage(
                messageRepository.loadMessages(conversationId, lastMessageId, pageable)
                        .map(MessageResponse::fromEntity)
        );
    }
}

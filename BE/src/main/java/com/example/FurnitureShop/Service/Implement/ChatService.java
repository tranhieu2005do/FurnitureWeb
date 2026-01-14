package com.example.FurnitureShop.Service.Implement;

import com.example.FurnitureShop.DTO.Request.ChatMessage;
import com.example.FurnitureShop.DTO.Response.ConservationResponse;
import com.example.FurnitureShop.Exception.NotFoundException;
import com.example.FurnitureShop.Model.Conservation;
import com.example.FurnitureShop.Model.Conservation.ConservationStatus;
import com.example.FurnitureShop.Model.Message;
import com.example.FurnitureShop.Model.Message.MessageType;
import com.example.FurnitureShop.Model.User;
import com.example.FurnitureShop.Repository.ConservationRepository;
import com.example.FurnitureShop.Repository.MessageRepository;
import com.example.FurnitureShop.Repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class ChatService {

    private final ConservationRepository conservationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Transactional
    @CacheEvict(allEntries = true)
    public Message saveMessage(ChatMessage message) {
        Conservation conservation = conservationRepository.findById(message.getConservationId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy cuộc trò chuyện."));
        User sender = userRepository.findById(message.getSenderId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thông tin người dùng này."));
        Message newMessage = Message.builder()
                .content(message.getContent())
                .sender(sender)
                .conservation(conservation)
                .createdAt(LocalDateTime.now())
                .type(MessageType.TEXT)
                .build();
        return messageRepository.save(newMessage);
    }

    @Transactional(readOnly = true)
    @Cacheable(key = "#userId")
    public ConservationResponse getOrCreateConservationByUserId(Long userId){

        User customer = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng."));

        if(conservationRepository.getConservationByUserId(userId) == null){
            Conservation newConservation = Conservation.builder()
                    .customer(customer)
                    .status(ConservationStatus.CLOSED)
                    .createdAt(LocalDateTime.now())
                    .messages(null)
                    .build();
            conservationRepository.save(newConservation);
            return ConservationResponse.fromEntity(newConservation);
        }

        return ConservationResponse.fromEntity(conservationRepository.getConservationByUserId(userId));
    }
}

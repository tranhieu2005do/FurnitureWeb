package com.example.FurnitureShop.Service.Implement;

import com.example.FurnitureShop.DTO.Request.NotificationRequest;
import com.example.FurnitureShop.DTO.Response.NotificationResponse;
import com.example.FurnitureShop.Exception.AuthException;
import com.example.FurnitureShop.Exception.NotFoundException;
import com.example.FurnitureShop.Model.Notification;
import com.example.FurnitureShop.Model.User;
import com.example.FurnitureShop.Repository.NotificationRepository;
import com.example.FurnitureShop.Repository.UserRepository;
import com.example.FurnitureShop.Service.Interface.INotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
@CacheConfig(cacheNames = "notifications")
@Slf4j
public class NotificationService implements INotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final PromotionService promotionService;

    @Override
    @Transactional
    @CacheEvict(allEntries = true)
    public NotificationResponse create(NotificationRequest notificationRequest) {
        User user = userRepository.findById(notificationRequest.getUserId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng."));

        if(!user.isActive()){
            throw new AuthException("Tài khoản người dùng này hiện đã bị khóa.");
        }
        Notification notification = Notification.builder()
                .user(user)
                .message(notificationRequest.getMessage())
                .topic(notificationRequest.getTopic())
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();
        notificationRepository.save(notification);

        return NotificationResponse.fromEntity(notification);
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(key = "#userId")
    public List<NotificationResponse> getByUserId(Long userId) {
        return notificationRepository.getByUserId(userId)
                .stream()
                .map(NotificationResponse::fromEntity)
                .toList();
    }

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Ho_Chi_Minh")
    @Transactional
    public void notifyPromotionOnBirthDay(){
        Integer month = LocalDate.now().getMonthValue();
        Integer day = LocalDate.now().getDayOfMonth();
        List<User> users = userRepository.findUsersInBirthday(month, day);
        for(User user : users){
            try{

//                promotionService.createBirthDayPromotionForUser(user);
                NotificationRequest request = NotificationRequest.builder()
                        .userId(user.getId())
                        .message("Nhân ngày sinh nhật của bạn, chúng tôi có ữu đãi khuyến mãi 25% giá trị cho đơn hàng của bạn trong tuần lễ sinh nhật này.")
                        .topic("HAPPY BIRTHDAY")
                        .build();
                create(request);
            }
            catch (Exception ex){
                log.error("Birthday promotion failed for user {}", user.getId(), ex);
            }
        }
    }
}

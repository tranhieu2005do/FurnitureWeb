package com.example.FurnitureShop.Config;

import com.example.FurnitureShop.Service.Implement.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@AllArgsConstructor
public class ScheduleConfig {
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Ho_Chi_Minh")
    public void BirthDayPromotion(){
        notificationService.notifyPromotionOnBirthDay();
    }
}

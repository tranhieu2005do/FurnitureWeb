package com.example.FurnitureShop.Config.Kafka.Topic.OrderCreated;

import com.example.FurnitureShop.DTO.Request.OrderCreatedMailRequest;
import com.example.FurnitureShop.Service.Implement.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class Notification {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "Order_Created",
            groupId = "order-notification-group"
    )
    public void listen(OrderCreatedMailRequest orderCreatedMailRequest,
                       Acknowledgment ack,
                       ConsumerRecord<?, ?> record)
    {
        log.info("Creating notification for the order.");
        ack.acknowledge();
        // Nhận tin order để tạo notification tạo đơn thành công trên web
        notificationService.create(orderCreatedMailRequest.getNotificationRequest());
    }
}

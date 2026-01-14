package com.example.FurnitureShop.Config.Kafka.Topic.OrderCreated;

import com.example.FurnitureShop.DTO.Request.OrderCreatedMailRequest;
import com.example.FurnitureShop.Service.Implement.AuthService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class Mail {

    private final AuthService authService;

    @KafkaListener(
            topics = "Order_Created",
            groupId = "order-mail-group"
    )
    public void listen(OrderCreatedMailRequest mailRequest,
                       Acknowledgment ack,
                       ConsumerRecord<?, ?> record) throws MessagingException
    {
        log.info("Received Mail Request is preparing to be sent to ", mailRequest.getTo());
        ack.acknowledge();
        // Gửi mail thông báo
        authService.sendOrderCreatedMail(mailRequest);
    }
}

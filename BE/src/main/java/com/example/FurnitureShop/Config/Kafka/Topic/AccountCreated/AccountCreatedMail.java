package com.example.FurnitureShop.Config.Kafka.Topic.AccountCreated;

import com.example.FurnitureShop.DTO.Request.MailRequest;
import com.example.FurnitureShop.Service.Implement.AuthService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountCreatedMail {

    private final AuthService authService;

    @KafkaListener(
            topics = "Mail",
            groupId = "account-mail-group"
    )
    public void listen(MailRequest mailRequest,
                       Acknowledgment ack) throws MessagingException
    {
        log.info("Sending authenticated mail for register successfully.!! {}", mailRequest.getTo());
        ack.acknowledge();
        authService.sendAuthenticatedMail(mailRequest);
    }
}

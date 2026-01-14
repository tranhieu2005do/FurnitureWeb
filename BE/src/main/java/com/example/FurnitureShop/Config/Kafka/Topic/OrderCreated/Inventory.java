package com.example.FurnitureShop.Config.Kafka.Topic.OrderCreated;

import com.example.FurnitureShop.DTO.Request.OrderCreatedMailRequest;
import com.example.FurnitureShop.Service.Implement.InventoryLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class Inventory {

    private final InventoryLogService  inventoryLogService;

    @KafkaListener(
            topics = "Order_Created",
            groupId = "order-inventory-group"
    )
    public void listen(OrderCreatedMailRequest mailRequest,
                       Acknowledgment ack,
                       ConsumerRecord<?, ?> record)
    {
        log.info("Handling inventory for the order");
        ack.acknowledge();
        // Xử lý instock và log của variant và product
        inventoryLogService.handleOrderCreated(mailRequest.getOrderItems());
    }
}

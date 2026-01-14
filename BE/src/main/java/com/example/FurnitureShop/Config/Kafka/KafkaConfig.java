package com.example.FurnitureShop.Config.Kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.JsonbMessageConverter;

@Configuration
public class KafkaConfig {

    @Bean
    NewTopic authenticatedMailTopic(){
        return new NewTopic("Mail", 2, (short) 1);
    }

    @Bean
    NewTopic orderCreatedTopic(){
        return new NewTopic("Order_Created", 2, (short) 1);
    }

//    @Bean
//    JsonbMessageConverter converter(){
//        return new JsonbMessageConverter();
//    }
}

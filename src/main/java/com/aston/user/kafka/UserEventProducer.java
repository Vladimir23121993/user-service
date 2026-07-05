package com.aston.user.kafka;

import com.aston.user.dto.UserEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserEventProducer {

    private static final Logger log = LoggerFactory.getLogger(UserEventProducer.class);
    private final KafkaTemplate<String, UserEventDto> kafkaTemplate;
    private static final String TOPIC = "user-events-2";

    public UserEventProducer(KafkaTemplate<String, UserEventDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(String email, String operation) {
        UserEventDto event = new UserEventDto(email, operation);
        kafkaTemplate.send(TOPIC, event);
        log.info("Событие отправлено в Kafka: {} -> {}", operation, email);
    }
}
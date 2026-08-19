package com.example.notificationservice.consumer;

import com.example.notificationservice.notification.NotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedListener {
    private final NotificationService service;
    private final ObjectMapper objectMapper;
    public OrderCreatedListener(NotificationService service, ObjectMapper objectMapper) { this.service = service; this.objectMapper = objectMapper; }
    @KafkaListener(topics = "order-created", groupId = "notification-service")
    public void onOrderCreated(String payload) {
        try { OrderCreatedEvent event = objectMapper.readValue(payload, OrderCreatedEvent.class); service.recordOrderContext(event.orderId(), event.customerId()); }
        catch (JsonProcessingException exception) { throw new IllegalArgumentException("Invalid order-created event", exception); }
    }
}

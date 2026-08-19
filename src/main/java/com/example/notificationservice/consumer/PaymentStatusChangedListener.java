package com.example.notificationservice.consumer;

import com.example.notificationservice.notification.NotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentStatusChangedListener {
    private final NotificationService service;
    private final ObjectMapper objectMapper;
    public PaymentStatusChangedListener(NotificationService service, ObjectMapper objectMapper) { this.service = service; this.objectMapper = objectMapper; }
    @KafkaListener(topics = "payment-status-changed", groupId = "notification-service")
    public void onPaymentStatusChanged(String payload) {
        try { PaymentStatusChangedEvent event = objectMapper.readValue(payload, PaymentStatusChangedEvent.class); service.handlePaymentStatusChanged(event.orderId(), event.status()); }
        catch (JsonProcessingException exception) { throw new IllegalArgumentException("Invalid payment-status-changed event", exception); }
    }
}

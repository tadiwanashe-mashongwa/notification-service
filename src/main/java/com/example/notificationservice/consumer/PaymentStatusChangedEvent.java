package com.example.notificationservice.consumer;
import java.util.UUID;
public record PaymentStatusChangedEvent(UUID paymentId, UUID orderId, String status) { }

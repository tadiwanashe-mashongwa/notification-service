package com.example.notificationservice.consumer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.UUID;
@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderCreatedEvent(UUID orderId, UUID customerId) { }

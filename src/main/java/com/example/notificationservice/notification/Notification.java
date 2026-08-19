package com.example.notificationservice.notification;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id private UUID id;
    private UUID customerId;
    private UUID orderId;
    @Enumerated(EnumType.STRING) private NotificationType type;
    private String message;
    private boolean read;
    private Instant createdAt;

    protected Notification() { }

    private Notification(UUID customerId, UUID orderId, NotificationType type, String message) {
        this.id = UUID.randomUUID();
        this.customerId = customerId;
        this.orderId = orderId;
        this.type = type;
        this.message = message;
        this.read = false;
        this.createdAt = Instant.now();
    }

    public static Notification paymentSucceeded(UUID customerId, UUID orderId) {
        return new Notification(customerId, orderId, NotificationType.PAYMENT_SUCCEEDED,
                "Payment received for order " + orderId);
    }

    public static Notification paymentFailed(UUID customerId, UUID orderId) {
        return new Notification(customerId, orderId, NotificationType.PAYMENT_FAILED,
                "Payment failed for order " + orderId);
    }

    public UUID getCustomerId() { return customerId; }
    public UUID getId() { return id; }
    public UUID getOrderId() { return orderId; }
    public NotificationType getType() { return type; }
    public String getMessage() { return message; }
    public boolean isRead() { return read; }
    public Instant getCreatedAt() { return createdAt; }
    public void markRead() { this.read = true; }
}

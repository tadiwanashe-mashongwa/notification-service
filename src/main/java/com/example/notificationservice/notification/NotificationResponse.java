package com.example.notificationservice.notification;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(UUID id, UUID orderId, NotificationType type, String message, boolean read, Instant createdAt) {
    static NotificationResponse from(Notification notification) {
        return new NotificationResponse(notification.getId(), notification.getOrderId(), notification.getType(), notification.getMessage(), notification.isRead(), notification.getCreatedAt());
    }
}

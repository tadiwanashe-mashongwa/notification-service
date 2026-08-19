package com.example.notificationservice.notification;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "pending_payment_notifications")
@IdClass(PendingPaymentNotification.Key.class)
public class PendingPaymentNotification {
    @Id private UUID orderId;
    @Id @Enumerated(EnumType.STRING) private NotificationType type;
    protected PendingPaymentNotification() { }
    private PendingPaymentNotification(UUID orderId, NotificationType type) { this.orderId = orderId; this.type = type; }
    public static PendingPaymentNotification create(UUID orderId, NotificationType type) { return new PendingPaymentNotification(orderId, type); }
    public NotificationType getType() { return type; }
    public static class Key implements Serializable {
        private UUID orderId;
        private NotificationType type;
    }
}

package com.example.notificationservice.notification;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "notification_orders")
public class NotificationOrder {
    @Id private UUID orderId;
    private UUID customerId;
    protected NotificationOrder() { }
    private NotificationOrder(UUID orderId, UUID customerId) { this.orderId = orderId; this.customerId = customerId; }
    public static NotificationOrder create(UUID orderId, UUID customerId) { return new NotificationOrder(orderId, customerId); }
    public UUID getCustomerId() { return customerId; }
}

package com.example.notificationservice.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PendingPaymentNotificationRepository extends JpaRepository<PendingPaymentNotification, PendingPaymentNotification.Key> {
    List<PendingPaymentNotification> findByOrderId(UUID orderId);
}

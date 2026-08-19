package com.example.notificationservice.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    boolean existsByOrderIdAndType(UUID orderId, NotificationType type);
}

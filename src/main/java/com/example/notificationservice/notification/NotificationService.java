package com.example.notificationservice.notification;

import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notifications;
    private final NotificationOrderRepository orders;
    public NotificationService(NotificationRepository notifications, NotificationOrderRepository orders) { this.notifications = notifications; this.orders = orders; }
    public void recordOrderContext(UUID orderId, UUID customerId) { if (!orders.existsById(orderId)) orders.save(NotificationOrder.create(orderId, customerId)); }
    public void handlePaymentStatusChanged(UUID orderId, String status) {
        NotificationType type = switch (status) { case "SUCCESS" -> NotificationType.PAYMENT_SUCCEEDED; case "FAILED" -> NotificationType.PAYMENT_FAILED; default -> null; };
        if (type == null || notifications.existsByOrderIdAndType(orderId, type)) return;
        orders.findById(orderId).ifPresent(order -> notifications.save(type == NotificationType.PAYMENT_SUCCEEDED
                ? Notification.paymentSucceeded(order.getCustomerId(), orderId)
                : Notification.paymentFailed(order.getCustomerId(), orderId)));
    }
    public List<Notification> listForCustomer(UUID customerId) { return notifications.findByCustomerIdOrderByCreatedAtDesc(customerId); }
    public Notification markRead(UUID customerId, UUID notificationId) {
        Notification notification = notifications.findById(notificationId).orElseThrow(NotificationNotFoundException::new);
        if (!notification.getCustomerId().equals(customerId)) throw new NotificationNotFoundException();
        notification.markRead();
        return notifications.save(notification);
    }
}

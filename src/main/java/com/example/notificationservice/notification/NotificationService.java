package com.example.notificationservice.notification;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.example.notificationservice.delivery.NotificationDelivery;
import java.util.UUID;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notifications;
    private final NotificationOrderRepository orders;
    private final PendingPaymentNotificationRepository pendingPaymentNotifications;
    private final NotificationDelivery notificationDelivery;
    @Autowired
    public NotificationService(NotificationRepository notifications, NotificationOrderRepository orders, PendingPaymentNotificationRepository pendingPaymentNotifications, NotificationDelivery notificationDelivery) {
        this.notifications = notifications; this.orders = orders; this.pendingPaymentNotifications = pendingPaymentNotifications; this.notificationDelivery = notificationDelivery;
    }
    NotificationService(NotificationRepository notifications, NotificationOrderRepository orders) {
        this(notifications, orders, null, notification -> { });
    }
    NotificationService(NotificationRepository notifications, NotificationOrderRepository orders, PendingPaymentNotificationRepository pendingPaymentNotifications) {
        this(notifications, orders, pendingPaymentNotifications, notification -> { });
    }
    @Transactional
    public void recordOrderContext(UUID orderId, UUID customerId) {
        if (!orders.existsById(orderId)) orders.save(NotificationOrder.create(orderId, customerId));
        if (pendingPaymentNotifications == null) return;
        for (PendingPaymentNotification pending : pendingPaymentNotifications.findByOrderId(orderId)) {
            createNotification(orderId, customerId, pending.getType());
            pendingPaymentNotifications.delete(pending);
        }
    }
    public void handlePaymentStatusChanged(UUID orderId, String status) {
        NotificationType type = switch (status) { case "SUCCESS" -> NotificationType.PAYMENT_SUCCEEDED; case "FAILED" -> NotificationType.PAYMENT_FAILED; default -> null; };
        if (type == null || notifications.existsByOrderIdAndType(orderId, type)) return;
        orders.findById(orderId).ifPresentOrElse(
                order -> createNotification(orderId, order.getCustomerId(), type),
                () -> { if (pendingPaymentNotifications != null) pendingPaymentNotifications.save(PendingPaymentNotification.create(orderId, type)); });
    }
    private void createNotification(UUID orderId, UUID customerId, NotificationType type) {
        if (notifications.existsByOrderIdAndType(orderId, type)) return;
        Notification notification = type == NotificationType.PAYMENT_SUCCEEDED
                ? Notification.paymentSucceeded(customerId, orderId)
                : Notification.paymentFailed(customerId, orderId);
        notifications.save(notification);
        notificationDelivery.deliver(notification);
    }
    public List<Notification> listForCustomer(UUID customerId) { return notifications.findByCustomerIdOrderByCreatedAtDesc(customerId); }
    public List<Notification> listUnreadForCustomer(UUID customerId) { return notifications.findByCustomerIdAndReadFalseOrderByCreatedAtDesc(customerId); }
    public Notification markRead(UUID customerId, UUID notificationId) {
        Notification notification = notifications.findById(notificationId).orElseThrow(NotificationNotFoundException::new);
        if (!notification.getCustomerId().equals(customerId)) throw new NotificationNotFoundException();
        notification.markRead();
        return notifications.save(notification);
    }
}

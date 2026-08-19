package com.example.notificationservice.notification;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificationServiceTest {
    @Test
    void createsOneUnreadNotificationForSuccessfulPayment() {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        NotificationRepository notifications = mock(NotificationRepository.class);
        NotificationOrderRepository orders = mock(NotificationOrderRepository.class);
        when(orders.findById(orderId)).thenReturn(Optional.of(NotificationOrder.create(orderId, customerId)));
        when(notifications.existsByOrderIdAndType(orderId, NotificationType.PAYMENT_SUCCEEDED)).thenReturn(false);
        NotificationService service = new NotificationService(notifications, orders);

        service.handlePaymentStatusChanged(orderId, "SUCCESS");

        verify(notifications).save(any(Notification.class));
    }

    @Test
    void marksOwnedNotificationAsRead() {
        UUID customerId = UUID.randomUUID();
        Notification notification = Notification.paymentSucceeded(customerId, UUID.randomUUID());
        NotificationRepository notifications = mock(NotificationRepository.class);
        NotificationOrderRepository orders = mock(NotificationOrderRepository.class);
        when(notifications.findById(notification.getId())).thenReturn(Optional.of(notification));
        when(notifications.save(notification)).thenReturn(notification);
        NotificationService service = new NotificationService(notifications, orders);

        Notification updated = service.markRead(customerId, notification.getId());

        assertThat(updated.isRead()).isTrue();
        verify(notifications).save(notification);
    }

    @Test
    void rejectsMarkingAnotherCustomersNotification() {
        Notification notification = Notification.paymentSucceeded(UUID.randomUUID(), UUID.randomUUID());
        NotificationRepository notifications = mock(NotificationRepository.class);
        NotificationOrderRepository orders = mock(NotificationOrderRepository.class);
        when(notifications.findById(notification.getId())).thenReturn(Optional.of(notification));
        NotificationService service = new NotificationService(notifications, orders);

        assertThatThrownBy(() -> service.markRead(UUID.randomUUID(), notification.getId()))
                .isInstanceOf(NotificationNotFoundException.class);
    }
}

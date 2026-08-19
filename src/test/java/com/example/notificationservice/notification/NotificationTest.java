package com.example.notificationservice.notification;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationTest {
    @Test
    void createsUnreadPaymentSuccessNotificationForCustomer() {
        UUID customerId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        Notification notification = Notification.paymentSucceeded(customerId, orderId);

        assertThat(notification.getCustomerId()).isEqualTo(customerId);
        assertThat(notification.getType()).isEqualTo(NotificationType.PAYMENT_SUCCEEDED);
        assertThat(notification.isRead()).isFalse();
    }
}

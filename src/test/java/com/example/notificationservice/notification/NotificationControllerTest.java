package com.example.notificationservice.notification;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Map;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class NotificationControllerTest {
    @Test
    void marksAuthenticatedCustomersNotificationRead() {
        UUID customerId = UUID.randomUUID();
        Notification notification = Notification.paymentSucceeded(customerId, UUID.randomUUID());
        notification.markRead();
        NotificationService service = mock(NotificationService.class);
        when(service.markRead(customerId, notification.getId())).thenReturn(notification);
        NotificationController controller = new NotificationController(service);
        Jwt jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(60), Map.of("alg", "none"), Map.of("sub", customerId.toString()));

        NotificationResponse response = controller.markRead(notification.getId(), jwt);

        assertThat(response.read()).isTrue();
        verify(service).markRead(customerId, notification.getId());
    }

    @Test
    void listsUnreadNotificationsForAuthenticatedCustomer() {
        UUID customerId = UUID.randomUUID();
        NotificationService service = mock(NotificationService.class);
        when(service.listUnreadForCustomer(customerId)).thenReturn(List.of(Notification.paymentSucceeded(customerId, UUID.randomUUID())));
        NotificationController controller = new NotificationController(service);
        Jwt jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(60), Map.of("alg", "none"), Map.of("sub", customerId.toString()));

        assertThat(controller.listUnreadMine(jwt)).hasSize(1);
        verify(service).listUnreadForCustomer(customerId);
    }
}

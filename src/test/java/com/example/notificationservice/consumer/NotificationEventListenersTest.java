package com.example.notificationservice.consumer;

import com.example.notificationservice.notification.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.*;

class NotificationEventListenersTest {
    @Test
    void recordsOrderContextFromOrderCreatedPayload() {
        NotificationService service = mock(NotificationService.class);
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        new OrderCreatedListener(service, new ObjectMapper()).onOrderCreated("{\"orderId\":\"%s\",\"customerId\":\"%s\",\"items\":[]}".formatted(orderId, customerId));

        verify(service).recordOrderContext(orderId, customerId);
    }

    @Test
    void handlesPaymentStatusFromPaymentPayload() {
        NotificationService service = mock(NotificationService.class);
        UUID orderId = UUID.randomUUID();

        new PaymentStatusChangedListener(service, new ObjectMapper()).onPaymentStatusChanged("{\"paymentId\":\"%s\",\"orderId\":\"%s\",\"status\":\"SUCCESS\"}".formatted(UUID.randomUUID(), orderId));

        verify(service).handlePaymentStatusChanged(orderId, "SUCCESS");
    }
}

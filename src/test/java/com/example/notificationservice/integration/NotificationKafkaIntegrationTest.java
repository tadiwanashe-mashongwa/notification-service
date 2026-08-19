package com.example.notificationservice.integration;

import com.example.notificationservice.notification.NotificationRepository;
import com.example.notificationservice.notification.NotificationOrderRepository;
import com.example.notificationservice.notification.NotificationType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Import(NotificationKafkaIntegrationTest.JwtTestConfiguration.class)
class NotificationKafkaIntegrationTest {
    @TestConfiguration static class JwtTestConfiguration {
        @Bean JwtDecoder jwtDecoder() { return token -> { throw new JwtException("JWT decoding is not used by this Kafka integration test"); }; }
    }
    @Container static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");
    @Container static final org.testcontainers.kafka.KafkaContainer kafka = new org.testcontainers.kafka.KafkaContainer("apache/kafka-native:3.8.0");
    @Autowired KafkaTemplate<String, String> kafkaTemplate;
    @Autowired NotificationRepository notifications;
    @Autowired NotificationOrderRepository orders;

    @DynamicPropertySource static void infrastructure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.kafka.consumer.auto-offset-reset", () -> "earliest");
        registry.add("spring.kafka.producer.key-serializer", () -> "org.apache.kafka.common.serialization.StringSerializer");
        registry.add("spring.kafka.producer.value-serializer", () -> "org.apache.kafka.common.serialization.StringSerializer");
    }

    @Test void persistsPaymentSuccessNotificationFromKafkaEvents() throws Exception {
        UUID orderId = UUID.randomUUID(); UUID customerId = UUID.randomUUID();
        kafkaTemplate.send("order-created", orderId.toString(), "{\"orderId\":\"%s\",\"customerId\":\"%s\",\"items\":[]}".formatted(orderId, customerId)).get();
        waitForOrderContext(orderId);
        kafkaTemplate.send("payment-status-changed", orderId.toString(), "{\"paymentId\":\"%s\",\"orderId\":\"%s\",\"status\":\"SUCCESS\"}".formatted(UUID.randomUUID(), orderId)).get();
        assertThat(waitForNotification(orderId).getType()).isEqualTo(NotificationType.PAYMENT_SUCCEEDED);
    }

    private void waitForOrderContext(UUID orderId) throws InterruptedException {
        Instant deadline = Instant.now().plus(Duration.ofSeconds(10));
        while (Instant.now().isBefore(deadline)) { if (orders.existsById(orderId)) return; Thread.sleep(100); }
        throw new AssertionError("Timed out waiting for notification order context");
    }

    private com.example.notificationservice.notification.Notification waitForNotification(UUID orderId) throws InterruptedException {
        Instant deadline = Instant.now().plus(Duration.ofSeconds(10));
        while (Instant.now().isBefore(deadline)) { var result = notifications.findAll().stream().filter(n -> n.getOrderId().equals(orderId)).findFirst(); if (result.isPresent()) return result.get(); Thread.sleep(100); }
        throw new AssertionError("Timed out waiting for notification");
    }
}

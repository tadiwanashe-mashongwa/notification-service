package com.example.notificationservice.notification;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService service;
    public NotificationController(NotificationService service) { this.service = service; }
    @GetMapping public List<NotificationResponse> listMine(@AuthenticationPrincipal Jwt jwt) {
        return service.listForCustomer(UUID.fromString(jwt.getSubject())).stream().map(NotificationResponse::from).toList();
    }
    @PatchMapping("/{notificationId}/read") public NotificationResponse markRead(@PathVariable UUID notificationId, @AuthenticationPrincipal Jwt jwt) {
        return NotificationResponse.from(service.markRead(UUID.fromString(jwt.getSubject()), notificationId));
    }
}

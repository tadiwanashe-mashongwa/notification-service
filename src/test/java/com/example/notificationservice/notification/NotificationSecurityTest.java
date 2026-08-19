package com.example.notificationservice.notification;

import com.example.notificationservice.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
@Import(SecurityConfig.class)
class NotificationSecurityTest {
    @Autowired private MockMvc mvc;
    @MockitoBean private NotificationService service;

    @Test
    void rejectsAnonymousNotificationInboxRequests() throws Exception {
        mvc.perform(get("/api/notifications/unread"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void permitsOpenApiPathWithoutAuthentication() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isNotFound());
    }
}

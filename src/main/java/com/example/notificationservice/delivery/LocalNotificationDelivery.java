package com.example.notificationservice.delivery;

import com.example.notificationservice.notification.Notification;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@ConditionalOnProperty(name = "notification.delivery.channel", havingValue = "local", matchIfMissing = true)
public class LocalNotificationDelivery implements NotificationDelivery {

    private static final Logger log = LoggerFactory.getLogger(LocalNotificationDelivery.class);

    @Override
    public void deliver(Notification notification) {
        log.info("Local notification delivery: notificationId={}, customerId={}, type={}",
                notification.getId(), notification.getCustomerId(), notification.getType());
    }
}

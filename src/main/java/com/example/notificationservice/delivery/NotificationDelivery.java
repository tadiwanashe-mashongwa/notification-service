package com.example.notificationservice.delivery;

import com.example.notificationservice.notification.Notification;

public interface NotificationDelivery {

    void deliver(Notification notification);
}

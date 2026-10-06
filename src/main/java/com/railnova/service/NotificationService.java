package com.railnova.service;

import com.railnova.entity.Notification;
import com.railnova.entity.User;

import java.util.List;

public interface NotificationService {
    Notification sendNotification(User user, String title, String message, String type);
    List<Notification> getUserNotifications(String userEmail);
    void markAsRead(Long notificationId);
}

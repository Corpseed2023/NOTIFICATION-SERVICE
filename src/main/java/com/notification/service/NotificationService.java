package com.notification.service;

import com.notification.dto.NotificationCreateRequestDto;
import com.notification.dto.NotificationResponseDto;
import com.notification.dto.UnreadCountResponseDto;
import com.notification.enums.NotificationEventType;
import com.notification.enums.NotificationModule;
import org.springframework.data.domain.Page;
import com.notification.service.websocket.NotificationWebSocketService;

public interface NotificationService {

    NotificationResponseDto createNotification(
            NotificationCreateRequestDto requestDto
    );

    Page<NotificationResponseDto> getUserNotifications(
            Long userId,
            int page,
            int size
    );

    Page<NotificationResponseDto> getUnreadNotifications(
            Long userId,
            int page,
            int size
    );

    UnreadCountResponseDto getUnreadCount(
            Long userId
    );

    NotificationResponseDto markAsRead(
            Long notificationId,
            Long userId
    );

    void markAllAsRead(
            Long userId
    );

    void deleteNotification(
            Long notificationId,
            Long userId
    );

    Page<NotificationResponseDto> getUserNotificationsByModule(
            Long userId,
            NotificationModule module,
            int page,
            int size
    );

    Page<NotificationResponseDto> getUserNotificationsByEventType(
            Long userId,
            NotificationEventType eventType,
            int page,
            int size
    );

    Page<NotificationResponseDto> getUserNotificationsByReference(
            Long userId,
            NotificationModule module,
            Long referenceId,
            int page,
            int size
    );

    Page<NotificationResponseDto> getReferenceNotifications(
            NotificationModule module,
            Long referenceId,
            int page,
            int size
    );

}
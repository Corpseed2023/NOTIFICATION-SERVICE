package com.notification.service.websocket;

import com.notification.dto.NotificationResponseDto;
import com.notification.dto.UnreadCountResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    /*
     * Sends live notification to one user.
     *
     * Frontend subscribes:
     * /topic/notifications/{userId}
     *
     * Example:
     * /topic/notifications/10
     */
    public void sendNotificationToUser(
            Long userId,
            NotificationResponseDto notification
    ) {
        if (userId == null || notification == null) {
            return;
        }

        messagingTemplate.convertAndSend(
                "/topic/notifications/" + userId,
                notification
        );
    }

    /*
     * Sends updated unread count to one user.
     *
     * Frontend subscribes:
     * /topic/notifications/{userId}/unread-count
     *
     * Example:
     * /topic/notifications/10/unread-count
     */
    public void sendUnreadCountToUser(
            Long userId,
            UnreadCountResponseDto unreadCountResponse
    ) {
        if (userId == null || unreadCountResponse == null) {
            return;
        }

        messagingTemplate.convertAndSend(
                "/topic/notifications/" + userId + "/unread-count",
                unreadCountResponse
        );
    }
}
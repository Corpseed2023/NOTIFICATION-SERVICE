package com.notification.dto;

import com.notification.enums.NotificationDisplayType;
import com.notification.enums.NotificationEventType;
import com.notification.enums.NotificationModule;
import com.notification.enums.NotificationPriority;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDto {

    private Long id;

    private Long receiverId;

    private Long actorId;

    private String actorName;

    private NotificationModule module;

    private NotificationEventType eventType;

    private Long referenceId;

    private String referenceNumber;

    private String title;

    private String message;

    private String redirectUrl;

    private NotificationPriority priority;

    private NotificationDisplayType displayType;

    private boolean read;

    private LocalDateTime readAt;

    private boolean deleted;

    private String metadataJson;

    private LocalDateTime createdAt;
}
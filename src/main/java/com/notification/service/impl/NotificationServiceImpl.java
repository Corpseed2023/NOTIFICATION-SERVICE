package com.notification.service.impl;

import com.notification.dto.NotificationCreateRequestDto;
import com.notification.dto.NotificationResponseDto;
import com.notification.dto.UnreadCountResponseDto;
import com.notification.entity.Notification;
import com.notification.enums.NotificationDisplayType;
import com.notification.enums.NotificationEventType;
import com.notification.enums.NotificationModule;
import com.notification.enums.NotificationPriority;
import com.notification.repository.NotificationRepository;
import com.notification.service.NotificationService;
import com.notification.service.websocket.NotificationWebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationWebSocketService notificationWebSocketService;

    @Override
    @Transactional
    public NotificationResponseDto createNotification(
            NotificationCreateRequestDto requestDto
    ) {
        validateCreateRequest(requestDto);

        Notification notification = Notification.builder()
                .receiverId(requestDto.getReceiverId())
                .actorId(requestDto.getActorId())
                .actorName(requestDto.getActorName())
                .module(requestDto.getModule())
                .eventType(requestDto.getEventType())
                .referenceId(requestDto.getReferenceId())
                .referenceNumber(requestDto.getReferenceNumber())
                .title(requestDto.getTitle())
                .message(requestDto.getMessage())
                .redirectUrl(requestDto.getRedirectUrl())
                .priority(requestDto.getPriority() != null ? requestDto.getPriority() : NotificationPriority.NORMAL)
                .displayType(requestDto.getDisplayType() != null ? requestDto.getDisplayType() : NotificationDisplayType.INFO)
                .read(false)
                .deleted(false)
                .metadataJson(requestDto.getMetadataJson())
                .build();

        Notification savedNotification = notificationRepository.save(notification);

        NotificationResponseDto responseDto = toDto(savedNotification);

        /*
         * Push live notification to frontend.
         */
        notificationWebSocketService.sendNotificationToUser(
                savedNotification.getReceiverId(),
                responseDto
        );

        /*
         * Push updated unread count to frontend.
         */
        sendUnreadCountUpdate(savedNotification.getReceiverId());

        return responseDto;
    }

    @Override
    public Page<NotificationResponseDto> getUserNotifications(
            Long userId,
            int page,
            int size
    ) {
        validateUserId(userId);

        Pageable pageable = createPageable(page, size);

        return notificationRepository
                .findByReceiverIdAndDeletedFalseOrderByCreatedAtDesc(userId, pageable)
                .map(this::toDto);
    }

    @Override
    public Page<NotificationResponseDto> getUnreadNotifications(
            Long userId,
            int page,
            int size
    ) {
        validateUserId(userId);

        Pageable pageable = createPageable(page, size);

        return notificationRepository
                .findByReceiverIdAndReadFalseAndDeletedFalseOrderByCreatedAtDesc(userId, pageable)
                .map(this::toDto);
    }

    @Override
    public UnreadCountResponseDto getUnreadCount(
            Long userId
    ) {
        validateUserId(userId);

        long unreadCount = notificationRepository
                .countByReceiverIdAndReadFalseAndDeletedFalse(userId);

        return UnreadCountResponseDto.builder()
                .userId(userId)
                .unreadCount(unreadCount)
                .build();
    }

    @Override
    @Transactional
    public NotificationResponseDto markAsRead(
            Long notificationId,
            Long userId
    ) {
        validateUserId(userId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + notificationId));

        validateReceiver(notification, userId);

        if (notification.isDeleted()) {
            throw new RuntimeException("Notification is already deleted");
        }

        if (!notification.isRead()) {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
            notification = notificationRepository.save(notification);

            /*
             * Push updated unread count after read.
             */
            sendUnreadCountUpdate(userId);
        }

        return toDto(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(
            Long userId
    ) {
        validateUserId(userId);

        List<Notification> unreadNotifications =
                notificationRepository.findByReceiverIdAndReadFalseAndDeletedFalse(userId);

        if (unreadNotifications == null || unreadNotifications.isEmpty()) {
            sendUnreadCountUpdate(userId);
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        for (Notification notification : unreadNotifications) {
            notification.setRead(true);
            notification.setReadAt(now);
        }

        notificationRepository.saveAll(unreadNotifications);

        /*
         * Push unread count as 0.
         */
        sendUnreadCountUpdate(userId);
    }

    @Override
    @Transactional
    public void deleteNotification(
            Long notificationId,
            Long userId
    ) {
        validateUserId(userId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + notificationId));

        validateReceiver(notification, userId);

        if (notification.isDeleted()) {
            return;
        }

        notification.setDeleted(true);
        notificationRepository.save(notification);

        /*
         * If deleted notification was unread, unread count should update.
         */
        sendUnreadCountUpdate(userId);
    }

    @Override
    public Page<NotificationResponseDto> getUserNotificationsByModule(
            Long userId,
            NotificationModule module,
            int page,
            int size
    ) {
        validateUserId(userId);

        if (module == null) {
            throw new RuntimeException("Notification module is required");
        }

        Pageable pageable = createPageable(page, size);

        return notificationRepository
                .findByReceiverIdAndModuleAndDeletedFalseOrderByCreatedAtDesc(userId, module, pageable)
                .map(this::toDto);
    }

    @Override
    public Page<NotificationResponseDto> getUserNotificationsByEventType(
            Long userId,
            NotificationEventType eventType,
            int page,
            int size
    ) {
        validateUserId(userId);

        if (eventType == null) {
            throw new RuntimeException("Notification event type is required");
        }

        Pageable pageable = createPageable(page, size);

        return notificationRepository
                .findByReceiverIdAndEventTypeAndDeletedFalseOrderByCreatedAtDesc(userId, eventType, pageable)
                .map(this::toDto);
    }

    @Override
    public Page<NotificationResponseDto> getUserNotificationsByReference(
            Long userId,
            NotificationModule module,
            Long referenceId,
            int page,
            int size
    ) {
        validateUserId(userId);

        if (module == null) {
            throw new RuntimeException("Notification module is required");
        }

        if (referenceId == null) {
            throw new RuntimeException("Reference id is required");
        }

        Pageable pageable = createPageable(page, size);

        return notificationRepository
                .findByReceiverIdAndModuleAndReferenceIdAndDeletedFalseOrderByCreatedAtDesc(
                        userId,
                        module,
                        referenceId,
                        pageable
                )
                .map(this::toDto);
    }

    @Override
    public Page<NotificationResponseDto> getReferenceNotifications(
            NotificationModule module,
            Long referenceId,
            int page,
            int size
    ) {
        if (module == null) {
            throw new RuntimeException("Notification module is required");
        }

        if (referenceId == null) {
            throw new RuntimeException("Reference id is required");
        }

        Pageable pageable = createPageable(page, size);

        return notificationRepository
                .findByModuleAndReferenceIdAndDeletedFalseOrderByCreatedAtDesc(
                        module,
                        referenceId,
                        pageable
                )
                .map(this::toDto);
    }

    private void sendUnreadCountUpdate(Long userId) {
        if (userId == null) {
            return;
        }

        long unreadCount = notificationRepository
                .countByReceiverIdAndReadFalseAndDeletedFalse(userId);

        UnreadCountResponseDto unreadCountResponseDto =
                UnreadCountResponseDto.builder()
                        .userId(userId)
                        .unreadCount(unreadCount)
                        .build();

        notificationWebSocketService.sendUnreadCountToUser(
                userId,
                unreadCountResponseDto
        );
    }

    private Pageable createPageable(
            int page,
            int size
    ) {
        int validPage = Math.max(page, 0);
        int validSize = size <= 0 ? 20 : Math.min(size, 100);

        return PageRequest.of(
                validPage,
                validSize,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
    }

    private void validateCreateRequest(
            NotificationCreateRequestDto requestDto
    ) {
        if (requestDto == null) {
            throw new RuntimeException("Notification request body is required");
        }

        if (requestDto.getReceiverId() == null) {
            throw new RuntimeException("Receiver id is required");
        }

        if (requestDto.getModule() == null) {
            throw new RuntimeException("Notification module is required");
        }

        if (requestDto.getEventType() == null) {
            throw new RuntimeException("Notification event type is required");
        }

        if (requestDto.getTitle() == null || requestDto.getTitle().trim().isEmpty()) {
            throw new RuntimeException("Notification title is required");
        }

        if (requestDto.getMessage() == null || requestDto.getMessage().trim().isEmpty()) {
            throw new RuntimeException("Notification message is required");
        }
    }

    private void validateUserId(
            Long userId
    ) {
        if (userId == null) {
            throw new RuntimeException("User id is required");
        }
    }

    private void validateReceiver(
            Notification notification,
            Long userId
    ) {
        if (notification.getReceiverId() == null) {
            throw new RuntimeException("Notification receiver not found");
        }

        if (!notification.getReceiverId().equals(userId)) {
            throw new RuntimeException("You are not allowed to access this notification");
        }
    }

    private NotificationResponseDto toDto(
            Notification notification
    ) {
        return NotificationResponseDto.builder()
                .id(notification.getId())
                .receiverId(notification.getReceiverId())
                .actorId(notification.getActorId())
                .actorName(notification.getActorName())
                .module(notification.getModule())
                .eventType(notification.getEventType())
                .referenceId(notification.getReferenceId())
                .referenceNumber(notification.getReferenceNumber())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .redirectUrl(notification.getRedirectUrl())
                .priority(notification.getPriority())
                .displayType(notification.getDisplayType())
                .read(notification.isRead())
                .readAt(notification.getReadAt())
                .deleted(notification.isDeleted())
                .metadataJson(notification.getMetadataJson())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
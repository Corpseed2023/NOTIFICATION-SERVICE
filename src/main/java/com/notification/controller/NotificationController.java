package com.notification.controller;

import com.notification.dto.NotificationCreateRequestDto;
import com.notification.dto.NotificationResponseDto;
import com.notification.dto.UnreadCountResponseDto;
import com.notification.enums.NotificationEventType;
import com.notification.enums.NotificationModule;
import com.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /*
     * Used by all other services:
     * lead-service, account-service, task-service, etc.
     *
     * POST /api/notifications
     */
    @PostMapping
    public ResponseEntity<?> createNotification(
            @RequestBody NotificationCreateRequestDto requestDto
    ) {
        try {
            NotificationResponseDto response =
                    notificationService.createNotification(requestDto);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
     * GET /api/notifications?userId=10&page=0&size=20
     */
    @GetMapping
    public ResponseEntity<?> getUserNotifications(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            Page<NotificationResponseDto> response =
                    notificationService.getUserNotifications(userId, page, size);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
     * GET /api/notifications/unread?userId=10&page=0&size=20
     */
    @GetMapping("/unread")
    public ResponseEntity<?> getUnreadNotifications(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            Page<NotificationResponseDto> response =
                    notificationService.getUnreadNotifications(userId, page, size);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
     * GET /api/notifications/unread-count?userId=10
     */
    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount(
            @RequestParam Long userId
    ) {
        try {
            UnreadCountResponseDto response =
                    notificationService.getUnreadCount(userId);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
     * PUT /api/notifications/{notificationId}/read?userId=10
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<?> markAsRead(
            @PathVariable Long notificationId,
            @RequestParam Long userId
    ) {
        try {
            NotificationResponseDto response =
                    notificationService.markAsRead(notificationId, userId);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
     * PUT /api/notifications/read-all?userId=10
     */
    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(
            @RequestParam Long userId
    ) {
        try {
            notificationService.markAllAsRead(userId);

            return new ResponseEntity<>("All notifications marked as read", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
     * DELETE /api/notifications/{notificationId}?userId=10
     */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> deleteNotification(
            @PathVariable Long notificationId,
            @RequestParam Long userId
    ) {
        try {
            notificationService.deleteNotification(notificationId, userId);

            return new ResponseEntity<>("Notification deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
     * GET /api/notifications/module/LEAD?userId=10&page=0&size=20
     */
    @GetMapping("/module/{module}")
    public ResponseEntity<?> getUserNotificationsByModule(
            @PathVariable NotificationModule module,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            Page<NotificationResponseDto> response =
                    notificationService.getUserNotificationsByModule(userId, module, page, size);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
     * GET /api/notifications/event/LEAD_ASSIGNED?userId=10&page=0&size=20
     */
    @GetMapping("/event/{eventType}")
    public ResponseEntity<?> getUserNotificationsByEventType(
            @PathVariable NotificationEventType eventType,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            Page<NotificationResponseDto> response =
                    notificationService.getUserNotificationsByEventType(userId, eventType, page, size);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
     * GET /api/notifications/reference/LEAD/55?userId=10&page=0&size=20
     */
    @GetMapping("/reference/{module}/{referenceId}")
    public ResponseEntity<?> getUserNotificationsByReference(
            @PathVariable NotificationModule module,
            @PathVariable Long referenceId,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            Page<NotificationResponseDto> response =
                    notificationService.getUserNotificationsByReference(
                            userId,
                            module,
                            referenceId,
                            page,
                            size
                    );

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
     * Admin/internal use:
     * GET /api/notifications/reference/LEAD/55/all?page=0&size=20
     */
    @GetMapping("/reference/{module}/{referenceId}/all")
    public ResponseEntity<?> getReferenceNotifications(
            @PathVariable NotificationModule module,
            @PathVariable Long referenceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            Page<NotificationResponseDto> response =
                    notificationService.getReferenceNotifications(
                            module,
                            referenceId,
                            page,
                            size
                    );

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
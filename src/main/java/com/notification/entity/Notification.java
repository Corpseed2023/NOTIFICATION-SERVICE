package com.notification.entity;

import com.notification.enums.NotificationDisplayType;
import com.notification.enums.NotificationEventType;
import com.notification.enums.NotificationModule;
import com.notification.enums.NotificationPriority;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(name = "idx_notification_receiver_id", columnList = "receiver_id"),
                @Index(name = "idx_notification_actor_id", columnList = "actor_id"),
                @Index(name = "idx_notification_module", columnList = "module"),
                @Index(name = "idx_notification_event_type", columnList = "event_type"),
                @Index(name = "idx_notification_reference", columnList = "module, reference_id"),
                @Index(name = "idx_notification_read", columnList = "is_read"),
                @Index(name = "idx_notification_deleted", columnList = "is_deleted"),
                @Index(name = "idx_notification_created_at", columnList = "created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * User who will receive this notification.
     *
     * This is only ID because this is a separate microservice.
     * Do not create User entity mapping here.
     */
    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    /*
     * User who performed the action.
     *
     * Nullable because some notifications can be system-generated.
     */
    @Column(name = "actor_id")
    private Long actorId;

    /*
     * Store actor name directly so notification-service does not need
     * to call user-service every time.
     */
    @Column(name = "actor_name", length = 150)
    private String actorName;

    /*
     * Module that created this notification.
     *
     * Example:
     * LEAD, PROPOSAL, ACCOUNT, TASK
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "module", nullable = false, length = 50)
    private NotificationModule module;

    /*
     * Exact business event.
     *
     * Example:
     * LEAD_ASSIGNED, PROPOSAL_SENT, PAYMENT_RECEIVED
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 80)
    private NotificationEventType eventType;

    /*
     * ID of the original entity from the source service.
     *
     * Example:
     * leadId = 55
     * proposalId = 88
     * invoiceId = 9001
     */
    @Column(name = "reference_id")
    private Long referenceId;

    /*
     * Human-readable reference number.
     *
     * Example:
     * LEAD-55, PROP-00021, INV-2026-001
     */
    @Column(name = "reference_number", length = 100)
    private String referenceNumber;

    /*
     * Notification heading.
     *
     * Example:
     * Lead Assigned To You
     */
    @Column(name = "title", nullable = false, length = 180)
    private String title;

    /*
     * Notification body.
     *
     * Example:
     * Rahul assigned GST Registration lead to you.
     */
    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    /*
     * Frontend URL where user should go after clicking notification.
     *
     * Example:
     * /leads/55
     * /proposals/88
     * /accounts/invoices/9001
     */
    @Column(name = "redirect_url", length = 500)
    private String redirectUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 30)
    private NotificationPriority priority;

    /*
     * Frontend display style.
     *
     * Example:
     * INFO, SUCCESS, WARNING, DANGER
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "display_type", nullable = false, length = 30)
    private NotificationDisplayType displayType;

    /*
     * Java field is read.
     * DB column is is_read.
     *
     * This avoids Lombok/Spring Data boolean confusion.
     */
    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    /*
     * Soft delete.
     *
     * Java field is deleted.
     * DB column is is_deleted.
     */
    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    /*
     * Extra flexible JSON string.
     *
     * Example:
     * {
     *   "leadName": "GST Registration",
     *   "clientName": "ABC Pvt Ltd"
     * }
     */
    @Lob
    @Column(name = "metadata_json", columnDefinition = "LONGTEXT")
    private String metadataJson;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (priority == null) {
            priority = NotificationPriority.NORMAL;
        }

        if (displayType == null) {
            displayType = NotificationDisplayType.INFO;
        }
    }
}
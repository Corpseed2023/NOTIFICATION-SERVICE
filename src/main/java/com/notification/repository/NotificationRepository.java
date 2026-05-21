package com.notification.repository;

import com.notification.entity.Notification;
import com.notification.enums.NotificationEventType;
import com.notification.enums.NotificationModule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByReceiverIdAndDeletedFalseOrderByCreatedAtDesc(
            Long receiverId,
            Pageable pageable
    );

    Page<Notification> findByReceiverIdAndReadFalseAndDeletedFalseOrderByCreatedAtDesc(
            Long receiverId,
            Pageable pageable
    );

    long countByReceiverIdAndReadFalseAndDeletedFalse(
            Long receiverId
    );

    List<Notification> findByReceiverIdAndReadFalseAndDeletedFalse(
            Long receiverId
    );

    Page<Notification> findByReceiverIdAndModuleAndDeletedFalseOrderByCreatedAtDesc(
            Long receiverId,
            NotificationModule module,
            Pageable pageable
    );

    Page<Notification> findByReceiverIdAndEventTypeAndDeletedFalseOrderByCreatedAtDesc(
            Long receiverId,
            NotificationEventType eventType,
            Pageable pageable
    );

    Page<Notification> findByReceiverIdAndModuleAndReferenceIdAndDeletedFalseOrderByCreatedAtDesc(
            Long receiverId,
            NotificationModule module,
            Long referenceId,
            Pageable pageable
    );

    Page<Notification> findByModuleAndReferenceIdAndDeletedFalseOrderByCreatedAtDesc(
            NotificationModule module,
            Long referenceId,
            Pageable pageable
    );
}
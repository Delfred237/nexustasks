package com.nexustasks.notification.repository;

import com.nexustasks.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByRecipientIdAndDeletedFalseOrderByCreatedAtDesc(Long recipientId, Pageable pageable);

    long countByRecipientIdAndDeletedFalseAndReadFalse(Long recipientId);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true, n.readAt = CURRENT_TIMESTAMP WHERE n.recipient.id = :recipientId AND n.read = false")
    void markAllAsRead(@Param("recipientId") Long recipientId);

    Optional<Notification> findByPublicIdAndDeletedFalse(String publicId);
}
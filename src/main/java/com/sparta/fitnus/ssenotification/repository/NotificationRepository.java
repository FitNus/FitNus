package com.sparta.fitnus.ssenotification.repository;

import com.sparta.fitnus.ssenotification.entity.SseNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<SseNotification, Long> {

    @Query("SELECT COUNT(n) FROM notifications n WHERE n.userId = :userId AND n.isRead = false")
    Long countUnreadNotifications(Long userId);

    @Query("SELECT n FROM notifications n WHERE n.userId = :userId AND n.isRead = false ORDER BY n.timestamp DESC ")
    Page<SseNotification> findUnreadNotificationsByUserId(Long userId, Pageable pageable);

    @Query("SELECT n FROM notifications n WHERE n.userId = :userId ORDER BY n.timestamp DESC")
    Page<SseNotification> findAllByUserId(Long userId, Pageable pageable);

    boolean existsByUserIdAndMessage(Long userId, String message);
}
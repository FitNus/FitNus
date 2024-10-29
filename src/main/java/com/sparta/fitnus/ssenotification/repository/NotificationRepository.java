package com.sparta.fitnus.ssenotification.repository;

import com.sparta.fitnus.ssenotification.entity.SseNotification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<SseNotification, Long> {

    @Query("SELECT COUNT(n) FROM notifications n WHERE n.userId = :userId AND n.isRead = false")
    Long countUnreadNotifications(Long userId);

    @Query("SELECT n FROM notifications n WHERE n.userId = :userId AND n.isRead = false ")
    List<SseNotification> findUnreadNotificationsByUserId(Long userId);

}
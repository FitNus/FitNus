package com.sparta.fitnus.ssenotification.repository;

import com.sparta.fitnus.ssenotification.entity.SseNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<SseNotification, Long> {
}

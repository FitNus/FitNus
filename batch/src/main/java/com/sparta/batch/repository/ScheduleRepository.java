package com.sparta.batch.repository;

import com.sparta.batch.dto.HistoryInfo;
import com.sparta.batch.entity.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("SELECT new com.sparta.batch.dto.HistoryInfo(" +
            "c.id, s.userId, c.centerName, u.nickname, s.scheduleName, s.startTime, s.endTime, s.requiredCoupon) " +
            "FROM Schedule s " +
            "JOIN Timeslot t ON s.timeslotId = t.id " +
            "JOIN Fitness f ON t.fitness.id = f.id " +
            "JOIN Center c ON f.center.id = c.id " +
            "JOIN User u ON s.userId = u.id " +
            "WHERE s.startTime >= :historyStartDateTime " +
            "AND s.startTime <= :historyEndDateTime " +
            "AND s.clubId IS NULL")
    Page<HistoryInfo> findAllByStartDateBetween(
            @Param("historyStartDateTime") LocalDateTime start,
            @Param("historyEndDateTime") LocalDateTime end,
            Pageable pageable);
}

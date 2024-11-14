package com.sparta.modulecommon.schedule.repository;

import com.sparta.modulecommon.center.dto.HistoryInfo;
import com.sparta.modulecommon.schedule.entity.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    boolean existsByUserIdAndStartTime(long userId, LocalDateTime startTime);

    @Query("SELECT s " +
            "FROM Schedule s " +
            "WHERE s.userId = :userId " +
            "AND YEAR(s.startTime) = :year " +
            "AND MONTH(s.startTime) = :month " +
            "AND :day IS NULL OR DAY(s.startTime) = :day")
    List<Schedule> findAllByUserIdYearAndMonthAndDay(@Param("userId") long userId, @Param("year") int year, @Param("month") int month, @Param("day") Integer day);

    List<Schedule> findByUserIdAndClubIdIsNullAndStartTimeBetween(long userId, LocalDateTime startOfOctober, LocalDateTime endOfOctober);

    @Query("SELECT new com.sparta.modulecommon.center.dto.HistoryInfo(" +
            "c.id, s.userId, u.nickname, s.scheduleName, s.startTime, s.endTime, s.requiredCoupon) " +
            "FROM Schedule s " +
            "JOIN Timeslot t ON s.timeslotId = t.id " +
            "JOIN Fitness f ON t.fitness.id = f.id " +
            "JOIN Center c ON f.center.id = c.id " +
            "JOIN User u ON s.userId = u.id " +
            "WHERE s.startTime >= :historyStartDateTime " +
            "AND s.startTime <= :historyEndDateTime " +
            "AND s.clubId IS NULL")
    Page<HistoryInfo> findAll(
            @Param("historyStartDateTime") LocalDateTime start,
            @Param("historyEndDateTime") LocalDateTime end,
            Pageable pageable);

    Integer countByTimeslotId(long timeslotId);
}

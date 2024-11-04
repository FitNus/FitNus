package com.sparta.modulecommon.schedule.repository;

import com.sparta.modulecommon.schedule.entity.Schedule;
import com.sparta.modulecommon.settlement.dto.SettlementResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    boolean existsByUserIdAndStartTime(long userId, LocalDateTime startTime);

    @Query("SELECT e " +
            "FROM Schedule e " +
            "WHERE e.userId = :userId " +
            "AND YEAR(e.startTime) = :year " +
            "AND MONTH(e.startTime) = :month " +
            "AND :day IS NULL OR DAY(e.startTime) = :day")
    List<Schedule> findAllByUserIdYearAndMonthAndDay(@Param("userId") long userId, @Param("year") int year, @Param("month") int month, @Param("day") Integer day);

    List<Schedule> findByUserIdAndClubIdIsNullAndStartTimeBetween(long userId, LocalDateTime startOfOctober, LocalDateTime endOfOctober);

    @Query("SELECT new com.sparta.modulecommon.settlement.dto.SettlementResult(c.id, SUM(s.requiredCoupon)) " +
            "FROM Schedule s " +
            "LEFT JOIN Timeslot t ON s.timeslotId = t.id " +
            "LEFT JOIN Fitness f ON t.fitness.id = f.id " +
            "LEFT JOIN Center c ON f.center.id = c.id " +
            "WHERE s.startTime >= :settlementStartDateTime " +
            "AND s.startTime <= :settlementEndDateTime " +
            "GROUP BY c.id")
    Page<SettlementResult> findAllByTimeslotIdAndRequiredCoupon(
            @Param("settlementStartDateTime") LocalDateTime start,
            @Param("settlementEndDateTime") LocalDateTime end,
            Pageable pageable);
}

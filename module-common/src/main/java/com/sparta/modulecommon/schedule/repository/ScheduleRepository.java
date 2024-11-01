package com.sparta.modulecommon.schedule.repository;

import com.sparta.modulecommon.schedule.entity.Schedule;
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
}

package com.sparta.fitnus.schedule.repository;

import com.sparta.fitnus.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    boolean existsByUserIdAndTimeslotId(long userId, long timeslotId);

    @Query("SELECT e FROM Schedule e WHERE e.userId = :userId AND MONTH(e.startTime) = :month")
    List<Schedule> findAllByUserIdAndMonth(@Param("userId") long userId, @Param("month") int month);

    @Query("SELECT e FROM Schedule e WHERE e.userId = :userId AND MONTH(e.startTime) = :month AND DAY(e.startTime) = :day")
    List<Schedule> findAllByUserIdAndMonthAndDay(@Param("userId") long userId, @Param("month") int month, @Param("day") int day);
}

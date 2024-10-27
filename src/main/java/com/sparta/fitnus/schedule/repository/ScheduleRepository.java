package com.sparta.fitnus.schedule.repository;

import com.sparta.fitnus.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("SELECT e FROM Schedule e WHERE MONTH(e.startTime) = :month AND e.userId = :userId")
    List<Schedule> findAllByUserIdMonthly(@Param("month") int month, @Param("userId") long userId);
}

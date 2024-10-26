package com.sparta.fitnus.schedule.repository;

import com.sparta.fitnus.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    
}

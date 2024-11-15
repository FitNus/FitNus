package com.sparta.service.schedule.repository;

import com.sparta.service.schedule.entity.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ScheduleBulkRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<Schedule> scheduleList) {
        String sql = "INSERT INTO schedule (start_time, user_id, required_coupon, timeslot_id) VALUES (?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql,
                scheduleList,
                scheduleList.size(),
                (PreparedStatement ps, Schedule schedule) -> {
                    ps.setObject(1, schedule.getStartTime());
                    ps.setLong(2, schedule.getUserId());
                    ps.setInt(3, schedule.getRequiredCoupon());
                    ps.setLong(4, schedule.getTimeslotId());
                });
    }
}

package com.sparta.service.timeslot.repository;

import com.sparta.service.timeslot.entity.Timeslot;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TimeslotBulkRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<Timeslot> timeslotList) {
        String sql = "INSERT INTO timeslot (fitness_id, start_time, capacity) VALUES (?, ?, ?)";

        jdbcTemplate.batchUpdate(sql,
                timeslotList,
                timeslotList.size(),
                (PreparedStatement ps, Timeslot timeslot) -> {
                    ps.setLong(1, timeslot.getFitness().getId());
                    ps.setObject(2, timeslot.getStartTime());
                    ps.setInt(3, timeslot.getCapacity());
                });
    }
}

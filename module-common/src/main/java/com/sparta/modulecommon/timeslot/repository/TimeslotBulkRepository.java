package com.sparta.modulecommon.timeslot.repository;

import com.sparta.modulecommon.timeslot.entity.Timeslot;
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
        String sql = "INSERT INTO timeslot (fitness_id, start_time, max_people, current_people) VALUES (?, ?, ?)";

        jdbcTemplate.batchUpdate(sql,
                timeslotList,
                timeslotList.size(),
                (PreparedStatement ps, Timeslot timeslot) -> {
                    ps.setLong(1, timeslot.getFitness().getId());
                    ps.setObject(2, timeslot.getStartTime());
                    ps.setInt(3, timeslot.getMaxPeople());
                });
    }
}

package com.sparta.service.fitness.repository;

import com.sparta.service.fitness.entity.Fitness;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FitnessBulkRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<Fitness> fitnessList) {
        String sql = "INSERT INTO fitness (fitness_name, required_coupon, center_id) VALUES (?, ?, ?)";

        jdbcTemplate.batchUpdate(sql,
                fitnessList,
                fitnessList.size(),
                (PreparedStatement ps, Fitness fitness) -> {
                    ps.setString(1, fitness.getFitnessName());
                    ps.setInt(2, fitness.getRequiredCoupon());
                    ps.setLong(3, fitness.getCenter().getId());
                });
    }
}

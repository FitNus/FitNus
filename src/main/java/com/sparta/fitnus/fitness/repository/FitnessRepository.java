package com.sparta.fitnus.fitness.repository;

import com.sparta.fitnus.fitness.entity.Fitness;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FitnessRepository extends JpaRepository<Fitness, Long> {
    // 특정 Fitness ID로 Center ID만 조회하는 메서드
    @Query("SELECT f.center.id FROM Fitness f WHERE f.id = :fitnessId")
    Optional<Long> findCenterIdByFitnessId(@Param("fitnessId") Long fitnessId);
}

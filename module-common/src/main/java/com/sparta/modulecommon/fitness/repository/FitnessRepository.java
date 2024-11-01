package com.sparta.modulecommon.fitness.repository;

import com.sparta.modulecommon.fitness.entity.Fitness;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FitnessRepository extends JpaRepository<Fitness, Long> {
    List<Fitness> findAllByCenterId(Long id);
}

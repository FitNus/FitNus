package com.sparta.fitnus.fitness.repository;

import com.sparta.fitnus.fitness.entity.Fitness;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FitnessRepository extends JpaRepository<Fitness, Long> {
    List<Fitness> findAllByCenterId(Long id);
}

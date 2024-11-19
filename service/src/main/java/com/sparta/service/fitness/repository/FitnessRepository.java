package com.sparta.service.fitness.repository;

import com.sparta.service.fitness.entity.Fitness;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FitnessRepository extends JpaRepository<Fitness, Long> {
    List<Fitness> findAllByCenterId(Long id);
}

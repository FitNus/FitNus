package com.sparta.fitnus.fitness.repository;

import com.sparta.fitnus.fitness.entity.Fitness;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FitnessRepository extends JpaRepository<Fitness, Long> {
}

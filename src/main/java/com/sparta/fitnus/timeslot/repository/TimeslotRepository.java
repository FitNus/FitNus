package com.sparta.fitnus.timeslot.repository;

import com.sparta.fitnus.timeslot.entity.Timeslot;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TimeslotRepository extends JpaRepository<Timeslot, Long> {
    @Query("SELECT t.fitness.id FROM Timeslot t WHERE t.id= :timeslotId")
    Optional<Long> findFitnessIdByTimeslotId(@Param("timeslotId") Long timeslotId);
}

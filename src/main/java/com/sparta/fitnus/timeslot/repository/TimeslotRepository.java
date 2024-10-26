package com.sparta.fitnus.timeslot.repository;

import com.sparta.fitnus.timeslot.entity.Timeslot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeslotRepository extends JpaRepository<Timeslot, Long> {
}

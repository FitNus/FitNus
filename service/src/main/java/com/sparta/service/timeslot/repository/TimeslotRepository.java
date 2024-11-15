package com.sparta.service.timeslot.repository;

import com.sparta.service.timeslot.entity.Timeslot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeslotRepository extends JpaRepository<Timeslot, Long> {
}

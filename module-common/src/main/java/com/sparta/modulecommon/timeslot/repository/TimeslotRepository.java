package com.sparta.modulecommon.timeslot.repository;

import com.sparta.modulecommon.timeslot.entity.Timeslot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeslotRepository extends JpaRepository<Timeslot, Long> {
}

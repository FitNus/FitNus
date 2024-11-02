package com.sparta.modulecommon.settlement.repository;

import com.sparta.modulecommon.settlement.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResultRepository extends JpaRepository<Result, Long> {

    Optional<Result> findByCenterId(long centerId);
}

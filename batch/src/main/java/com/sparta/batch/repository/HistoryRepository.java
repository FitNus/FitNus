package com.sparta.batch.repository;

import com.sparta.batch.dto.SettlementResult;
import com.sparta.batch.entity.History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {

    @Query("SELECT new com.sparta.batch.dto.SettlementResult(h.centerId, h.centerName, SUM(h.revenue), SUM(h.commission)) " +
            "FROM History h " +
            "WHERE h.startTime >= :settlementStartDateTime " +
            "AND h.startTime <= :settlementEndDateTime " +
            "GROUP BY h.centerId, h.centerName")
    Page<SettlementResult> findAllCalculated(
            @Param("settlementStartDateTime") LocalDateTime start,
            @Param("settlementEndDateTime") LocalDateTime end,
            Pageable pageable);

    @Query("SELECT DISTINCT h.centerId FROM History h")
    List<Long> findDistinctCenterIds();
}

package com.sparta.modulecommon.center.repository;

import com.sparta.modulecommon.center.dto.SettlementResult;
import com.sparta.modulecommon.center.entity.History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {

    @Query("SELECT new com.sparta.modulecommon.center.dto.SettlementResult(h.centerId, c.centerName, SUM(h.revenue), SUM(h.commission)) " +
            "FROM History h " +
            "JOIN Center c ON c.id = h.centerId " +
            "WHERE h.startTime >= :settlementStartDateTime " +
            "AND h.startTime <= :settlementEndDateTime " +
            "GROUP BY h.centerId")
    Page<SettlementResult> findAllCalculated(
            @Param("settlementStartDateTime") LocalDateTime start,
            @Param("settlementEndDateTime") LocalDateTime end,
            Pageable pageable);

    @Query("SELECT DISTINCT h.centerId FROM History h")
    List<Long> findDistinctCenterIds();
}

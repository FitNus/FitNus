package com.sparta.modulecommon.center.repository;

import com.sparta.modulecommon.center.entity.Center;
import com.sparta.modulecommon.center.exception.CenterNotFoundException;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CenterRepository extends JpaRepository<Center, Long> {

    default Center findCenterById(Long id) {
        return findById(id).orElseThrow(
                CenterNotFoundException::new
        );
    }

    // 특정 Center ID로 소유자 ID만 조회하는 메서드
    @Query("SELECT c.ownerId FROM Center c WHERE c.id = :centerId")
    Optional<Long> findOwnerIdByCenterId(@Param("centerId") Long centerId);
}

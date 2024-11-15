package com.sparta.service.center.repository;

import com.sparta.service.center.entity.Center;
import com.sparta.service.center.exception.CenterNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

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

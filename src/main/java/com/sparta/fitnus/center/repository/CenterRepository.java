package com.sparta.fitnus.center.repository;

import com.sparta.fitnus.center.entity.Center;
import com.sparta.fitnus.center.exception.CenterNotFoundException;
import com.sparta.fitnus.common.exception.NotFoundException;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CenterRepository extends JpaRepository<Center, Long>, CenterQueryRepository {

    default Center findCenterById(Long id) {
        return findById(id).orElseThrow(
                CenterNotFoundException::new
        );
    }

    // 특정 Center ID로 소유자 ID만 조회하는 메서드
    @Query("SELECT c.ownerId FROM Center c WHERE c.id = :centerId")
    Optional<Long> findOwnerIdByCenterId(@Param("centerId") Long centerId);
}

package com.sparta.fitnus.center.repository;

import com.sparta.fitnus.center.entity.Center;
import com.sparta.fitnus.common.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CenterRepository extends JpaRepository<Center, Long> {
    default Center findCenterById(Long id) {
        return findById(id).orElseThrow(
                () -> new NotFoundException("Center with id " + id + " not found")
        );
    }
}

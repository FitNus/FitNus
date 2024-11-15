package com.sparta.service.club.repository;

import com.sparta.service.club.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubRepository extends JpaRepository<Club, Long>, ClubQueryRepository {

    boolean existsByClubName(String clubName);
}

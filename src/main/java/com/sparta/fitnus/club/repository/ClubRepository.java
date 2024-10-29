package com.sparta.fitnus.club.repository;

import com.sparta.fitnus.club.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubRepository extends JpaRepository<Club, Long>, ClubQueryRepository {

    boolean existsByClubName(String clubName);
}

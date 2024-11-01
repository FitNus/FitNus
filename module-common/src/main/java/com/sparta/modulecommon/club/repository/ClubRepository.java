package com.sparta.modulecommon.club.repository;

import com.sparta.modulecommon.club.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubRepository extends JpaRepository<Club, Long>, ClubQueryRepository {

    boolean existsByClubName(String clubName);
}

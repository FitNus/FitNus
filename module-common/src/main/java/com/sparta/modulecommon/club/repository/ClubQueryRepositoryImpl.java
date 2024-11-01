package com.sparta.modulecommon.club.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.modulecommon.search.dto.response.SearchClubResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sparta.modulecommon.club.entity.QClub.club;


@Repository
@RequiredArgsConstructor
public class ClubQueryRepositoryImpl implements ClubQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<SearchClubResponse> search(
            String clubName,
            String clubInfo,
            String place,
            Pageable pageable
    ) {
        Long totalCount = jpaQueryFactory
                .select(Wildcard.count)
                .from(club)
                .where(
                        clubNameContains(clubName),
                        clubInfoContains(clubInfo),
                        placeContains(place)
                ).fetchOne();

        List<SearchClubResponse> searchClubResponses = jpaQueryFactory
                .select(
                        Projections.constructor(
                                SearchClubResponse.class,
                                club.id,
                                club.clubName,
                                club.clubInfo,
                                club.place,
                                club.date
                        )
                )
                .from(club)
                .where(
                        clubNameContains(clubName),
                        clubInfoContains(clubInfo),
                        placeContains(place)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(club.id.desc())
                .fetch();

        return new PageImpl<>(searchClubResponses, pageable, totalCount);
    }

    private BooleanExpression clubNameContains(String clubName) {
        return clubName != null ? club.clubName.containsIgnoreCase(clubName) : null;
    }

    private BooleanExpression clubInfoContains(String clubInfo) {
        return clubInfo != null ? club.clubInfo.containsIgnoreCase(clubInfo) : null;
    }

    private BooleanExpression placeContains(String place) {
        return place != null ? club.place.containsIgnoreCase(place) : null;
    }
}

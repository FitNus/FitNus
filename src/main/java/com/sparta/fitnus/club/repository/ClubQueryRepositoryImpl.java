package com.sparta.fitnus.club.repository;

import static com.sparta.fitnus.club.entity.QClub.club;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.fitnus.search.dto.response.SearchClubResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

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
                .groupBy(club.id)
                .orderBy(club.id.desc())
                .fetch();

        Long totalCount = jpaQueryFactory
                .select(Wildcard.count)
                .from(club)
                .where(
                        clubNameContains(clubName),
                        clubInfoContains(clubInfo),
                        placeContains(place)
                ).fetchOne();

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

//package com.sparta.modulecommon.center.repository;
//
//import com.querydsl.core.types.Projections;
//import com.querydsl.core.types.dsl.BooleanExpression;
//import com.querydsl.core.types.dsl.Wildcard;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import com.sparta.modulecommon.search.dto.response.SearchCenterResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//import static com.sparta.modulecommon.center.entity.QCenter.center;
//import static com.sparta.modulecommon.fitness.entity.QFitness.fitness;
//
//
//@Repository
//@RequiredArgsConstructor
//public class CenterQueryRepositoryImpl implements CenterQueryRepository {
//
//    private final JPAQueryFactory jpaQueryFactory;
//
//    @Override
//    public Page<SearchCenterResponse> search(
//            String centerName,
//            String fitnessName,
//            Pageable pageable
//    ) {
//        Long totalCount = jpaQueryFactory
//                .select(Wildcard.count)
//                .from(center)
//                .leftJoin(center.fitnesses, fitness)
//                .where(
//                        centerNameContains(centerName),
//                        fitnessNameContains(fitnessName)
//                ).fetchOne();
//
//        List<SearchCenterResponse> searchCenterResponse = jpaQueryFactory
//                .select(
//                        Projections.constructor(
//                                SearchCenterResponse.class,
//                                center.id,
//                                center.centerName,
//                                fitness.fitnessName
//                        )
//                )
//                .from(center)
//                .leftJoin(center.fitnesses, fitness)
//                .where(
//                        centerNameContains(centerName),
//                        fitnessNameContains(fitnessName)
//                )
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .orderBy(center.id.desc())
//                .fetch();
//
//        return new PageImpl<>(searchCenterResponse, pageable, totalCount);
//    }
//
//    private BooleanExpression centerNameContains(String centerName) {
//        return centerName != null ? center.centerName.containsIgnoreCase(centerName) : null;
//    }
//
//    private BooleanExpression fitnessNameContains(String fitnessName) {
//        return fitnessName != null && !fitnessName.isEmpty()
//                ? fitness.fitnessName.containsIgnoreCase(fitnessName) : null;
//    }
//}

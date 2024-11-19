package com.sparta.service.search.service;

import com.sparta.service.center.entity.CenterSearch;
import com.sparta.service.club.repository.ClubRepository;
import com.sparta.service.search.dto.response.SearchCenterResponse;
import com.sparta.service.search.dto.response.SearchClubResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.erhlc.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.client.erhlc.NativeSearchQuery;
import org.springframework.data.elasticsearch.client.erhlc.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final ClubRepository clubRepository;
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    public Page<SearchClubResponse> searchClubs(String clubName, String clubInfo, String place,
            int page, int size) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("modifiedAt").descending());

        return clubRepository.search(clubName, clubInfo, place, pageable);
    }

    // 주변 센터 검색 (GeoDistance)
    public Page<SearchCenterResponse> searchCentersNearByLocation(
            String centerName, String fitnessName, double lat, double lon, double radius, int page,
            int size) {

        // 페이지 설정
        Pageable pageable = PageRequest.of(page - 1, size);

        // GeoDistance 쿼리 생성
        GeoDistanceQueryBuilder geoDistanceQuery = QueryBuilders.geoDistanceQuery("location")
                .point(lat, lon)
                .distance(radius, DistanceUnit.KILOMETERS);  // Radius 설정 (단위: km)

        // BoolQueryBuilder와 GeoDistanceQuery 결합
        var boolQuery = QueryBuilders.boolQuery()
                .filter(geoDistanceQuery);

        // centerName이나 fitnessName이 null이 아닐 경우에만 조건 추가
        if (centerName != null && !centerName.isEmpty()) {
            boolQuery.must(QueryBuilders.multiMatchQuery(centerName, "centerName")
                    .field("centerName.ngram", 2.0f)     // ngram 필드 추가(문자열을 작은 단위로 쪼개서 인덱싱하는 방식)
                    .type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
                    .fuzziness(Fuzziness.AUTO)   // 퍼지 검색 추가(오타나 비슷한 단어를 허용하는 검색 방식)
                    .operator(Operator.OR));
        }
        if (fitnessName != null && !fitnessName.isEmpty()) {
            boolQuery.must(QueryBuilders.multiMatchQuery(fitnessName, "fitnessName")
                    .field("fitnessName.ngram", 2.0f)
                    .type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
                    .fuzziness(Fuzziness.AUTO)
                    .operator(Operator.OR));
        }

        // NativeSearchQuery 객체 생성 및 정렬 (Query 대신 사용)
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQuery) // boolQuery는 여러 필터나 조건을 결합한 복합 쿼리
                .withPageable(pageable)
                .withSort(SortBuilders // 정렬 조건 설정: 거리 기준으로 정렬을 설정
                        .geoDistanceSort("location", lat, lon)
                        .order(SortOrder.ASC)
                        .unit(DistanceUnit.KILOMETERS))
                .withFields("id", "centerName", "address", "fitnessName")  // 필요한 필드만 조회
                .withRequestCache(true);
        NativeSearchQuery searchQuery = searchQueryBuilder.build();

        // 실제 검색 수행
        SearchHits<CenterSearch> searchHits = elasticsearchRestTemplate.search(
                searchQuery, CenterSearch.class);

        // 검색된 결과를 DTO로 변환하여 반환
        List<SearchCenterResponse> responseList = searchHits.stream()
                .map(hit -> {
                    List<String> fitnesses = hit.getContent().getFitnessName();

                    // 검색어가 없거나 비어있으면 모든 피트니스 반환
                    if (fitnessName == null || fitnessName.isEmpty()) {
                        return new SearchCenterResponse(
                                hit.getContent().getId(),
                                hit.getContent().getCenterName(),
                                hit.getContent().getAddress(),
                                !fitnesses.isEmpty() ? fitnesses : List.of("피트니스 정보가 없습니다.")
                        );
                    }

                    // 검색어와 일치하는 피트니스만 필터링
                    List<String> matchedFitnesses = fitnesses.stream()
                            .filter(fitness -> fitness.contains(fitnessName))
                            .toList();

                    return new SearchCenterResponse(
                            hit.getContent().getId(),
                            hit.getContent().getCenterName(),
                            hit.getContent().getAddress(),
                            !matchedFitnesses.isEmpty() ? matchedFitnesses
                                    : List.of("피트니스 정보가 없습니다.")
                    );
                })
                .toList();

        // Page로 변환해서 반환
        return new PageImpl<>(responseList, pageable, searchHits.getTotalHits());
    }
}

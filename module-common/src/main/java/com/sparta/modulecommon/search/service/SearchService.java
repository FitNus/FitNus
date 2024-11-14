package com.sparta.modulecommon.search.service;

import com.sparta.modulecommon.center.entity.CenterSearch;
import com.sparta.modulecommon.club.repository.ClubRepository;
import com.sparta.modulecommon.common.service.ElasticsearchService;
import com.sparta.modulecommon.search.dto.response.SearchCenterResponse;
import com.sparta.modulecommon.search.dto.response.SearchClubResponse;
import java.util.List;
import java.util.stream.Collectors;
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
    private final ElasticsearchService elasticsearchService;

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
                    .field("centerName.ngram")     // ngram 필드 추가(문자열을 작은 단위로 쪼개서 인덱싱하는 방식)
                    .type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
                    .fuzziness(Fuzziness.AUTO)   // 퍼지 검색 추가(오타나 비슷한 단어를 허용하는 검색 방식)
                    .operator(Operator.OR));
        }
        if (fitnessName != null && !fitnessName.isEmpty()) {
            boolQuery.should(QueryBuilders.multiMatchQuery(fitnessName, "fitnesses.fitnessName")
                    .field("fitnesses.fitnessName.ngram")
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
                        .unit(DistanceUnit.KILOMETERS));
        NativeSearchQuery searchQuery = searchQueryBuilder.build();

        // 실제 검색 수행
        SearchHits<CenterSearch> searchHits = elasticsearchRestTemplate.search(
                searchQuery, CenterSearch.class);

        // 검색된 결과를 DTO로 변환하여 반환
        List<SearchCenterResponse> responseList = searchHits.stream()
                .map(hit -> new SearchCenterResponse(
                        hit.getContent().getId(),
                        hit.getContent().getCenterName(),
                        hit.getContent().getAddress(),
                        hit.getContent().getFitnessName() != null && !hit.getContent()
                                .getFitnessName()
                                .isEmpty() ? hit.getContent().getFitnessName()
                                : List.of("피트니스 정보가 없습니다.")
                ))
                .collect(Collectors.toList());

        // Page로 변환해서 반환
        return new PageImpl<>(responseList, pageable, searchHits.getTotalHits());
    }

    // 센터 생성 시 Elasticsearch에 정보 저장
    public void saveCenterSearch(CenterSearch centerSearch) {
        elasticsearchService.saveCenterSearch(centerSearch);  // ElasticsearchService 호출
    }
}

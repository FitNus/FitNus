//package com.sparta.service.center.repository;
//
//import com.sparta.service.center.entity.Center;
//import com.sparta.service.center.entity.CenterSearch;
//import org.springframework.data.domain.Page;
//import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public interface CenterSearchRepository extends ElasticsearchRepository<CenterSearch, Long> {
//
//    // GeoDistance 쿼리를 사용하여 위치를 기준으로 센터를 검색하는 메서드
//    @Query("{\"bool\": {\"filter\": ["
//            + "{\"geo_distance\": {\"distance\": \"?0km\", \"location\": {\"lat\": ?1, \"lon\": ?2}}}, "
//            + "{\"match\": {\"centerName\": \"?3\"}}, "
//            + "{\"nested\": {"
//            + "\"path\": \"fitnesses\", "
//            + "\"query\": {\"match\": {\"fitnesses.fitnessName\": \"?4\"}}}}]}}")
//    Page<Center> findCentersNearByLocation(double radius, double lat, double lon, String centerName,
//                                           String fitnessName, Pageable pageable);
//}

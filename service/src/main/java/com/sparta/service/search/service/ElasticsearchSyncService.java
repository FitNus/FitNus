package com.sparta.service.search.service;

import com.sparta.service.center.entity.Center;
import com.sparta.service.center.entity.CenterSearch;
import com.sparta.service.center.repository.CenterRepository;
import com.sparta.service.schedule.entity.Schedule;
import com.sparta.service.schedule.entity.ScheduleSearch;
import com.sparta.service.schedule.repository.ScheduleRepository;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.erhlc.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticsearchSyncService {

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;
    private final ScheduleRepository scheduleRepository;
    private final CenterRepository centerRepository;

    public void syncSchedulesToElasticsearch() {
        try {
            log.info("스케줄 동기화 시작");

            int batchSize = 1000;  // 한 번에 처리할 데이터 수
            long totalCount = scheduleRepository.count();
            int totalPages = (int) Math.ceil((double) totalCount / batchSize);

            log.info("동기화할 총 스케줄 수: {}", totalCount);

            for (int page = 0; page < totalPages; page++) {
                PageRequest pageRequest = PageRequest.of(page, batchSize);
                Page<Schedule> schedulePage = scheduleRepository.findAll(pageRequest);

                BulkRequest bulkRequest = new BulkRequest();

                for (Schedule schedule : schedulePage.getContent()) {
                    try {
                        ScheduleSearch scheduleSearch = new ScheduleSearch(schedule);
                        IndexRequest indexRequest = new IndexRequest("schedule")
                                .id(schedule.getId().toString())
                                .source(convertToMap(scheduleSearch));
                        bulkRequest.add(indexRequest);
                    } catch (Exception e) {
                        log.error("스케줄 처리 중 오류 발생 ID {}: {}", schedule.getId(),
                                e.getMessage());
                    }
                }

                if (bulkRequest.numberOfActions() > 0) {
                    log.info("배치 동기화 진행 중 {} / {}", page + 1, totalPages);
                    elasticsearchRestTemplate.execute(client ->
                            client.bulk(bulkRequest, RequestOptions.DEFAULT)
                    );
                }
            }

            log.info("동기화가 성공적으로 완료되었습니다");

        } catch (Exception e) {
            log.error("엘라스틱서치 동기화 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("엘라스틱서치 데이터 동기화 실패", e);
        }
    }

    public void syncCentersToElasticsearch() {
        try {
            log.info("센터 동기화 시작");

            int batchSize = 1000;
            long totalCount = centerRepository.count();
            int totalPages = (int) Math.ceil((double) totalCount / batchSize);

            log.info("동기화할 총 센터 수: {}", totalCount);

            for (int page = 0; page < totalPages; page++) {
                PageRequest pageRequest = PageRequest.of(page, batchSize);
                Page<Center> centerPage = centerRepository.findAll(pageRequest);

                BulkRequest bulkRequest = new BulkRequest();

                for (Center center : centerPage.getContent()) {
                    try {
                        CenterSearch centerSearch = new CenterSearch(center);
                        IndexRequest indexRequest = new IndexRequest("center")
                                .id(center.getId().toString())
                                .source(convertCenterToMap(centerSearch));
                        bulkRequest.add(indexRequest);
                    } catch (Exception e) {
                        log.error("센터 처리 중 오류 발생 ID {}: {}", center.getId(),
                                e.getMessage());
                    }
                }

                if (bulkRequest.numberOfActions() > 0) {
                    log.info("배치 동기화 진행 중 {} / {}", page + 1, totalPages);
                    elasticsearchRestTemplate.execute(client ->
                            client.bulk(bulkRequest, RequestOptions.DEFAULT)
                    );
                }
            }

            log.info("센터 동기화가 성공적으로 완료되었습니다");

        } catch (Exception e) {
            log.error("센터 엘라스틱서치 동기화 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("센터 엘라스틱서치 데이터 동기화 실패", e);
        }
    }

    private Map<String, Object> convertToMap(ScheduleSearch scheduleSearch) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", scheduleSearch.getId());
        map.put("userId", scheduleSearch.getUserId());
        map.put("timeslotId", scheduleSearch.getTimeslotId());
        map.put("clubId", scheduleSearch.getClubId());
        map.put("scheduleName", scheduleSearch.getScheduleName());
        map.put("startTime", scheduleSearch.getStartTime());
        map.put("endTime", scheduleSearch.getEndTime());
        map.put("requiredCoupon", scheduleSearch.getRequiredCoupon());
        map.put("year", scheduleSearch.getYear());
        map.put("month", scheduleSearch.getMonth());
        map.put("day", scheduleSearch.getDay());
        return map;
    }

    private Map<String, Object> convertCenterToMap(CenterSearch centerSearch) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", centerSearch.getId());
        map.put("centerName", centerSearch.getCenterName());
        map.put("fitnessName", centerSearch.getFitnessName());
        map.put("address", centerSearch.getAddress());

        // GeoPoint를 위도/경도 Map으로 변환
        Map<String, Double> location = new HashMap<>();
        location.put("lat", centerSearch.getLocation().getLat());
        location.put("lon", centerSearch.getLocation().getLon());
        map.put("location", location);

        map.put("latitude", centerSearch.getLatitude());
        map.put("longitude", centerSearch.getLongitude());
        return map;
    }
}

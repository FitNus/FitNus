package com.sparta.service.search.service;

import com.sparta.service.center.entity.CenterSearch;
import com.sparta.service.schedule.entity.ScheduleSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.erhlc.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ElasticsearchService {

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    public void saveSearch(CenterSearch centerSearch) {
        elasticsearchRestTemplate.save(centerSearch);
    }

    public void saveSearch(ScheduleSearch scheduleSearch) {
        elasticsearchRestTemplate.save(scheduleSearch);
    }

    public void deleteSearch(String id, Class<?> clazz) {
        elasticsearchRestTemplate.delete(id, clazz);
    }
}

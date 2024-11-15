package com.sparta.service.search.service;

import com.sparta.service.center.entity.CenterSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.erhlc.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ElasticsearchService {

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    // CenterSearch 인덱스를 저장할 때 자동으로 인덱스 생성
    public void saveCenterSearch(CenterSearch centerSearch) {
        elasticsearchRestTemplate.save(centerSearch);
    }

    public void saveFitnessName(CenterSearch centerSearch) {
        elasticsearchRestTemplate.save(centerSearch);
    }
}

package com.sparta.service.search.controller;

import com.sparta.common.apipayload.ApiResponse;
import com.sparta.service.search.service.ElasticsearchSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SearchSyncController {

    private final ElasticsearchSyncService elasticsearchSyncService;

    @PostMapping("/v1/schedules/sync-elasticsearch")
    public ApiResponse<Void> syncSchedules() {
        elasticsearchSyncService.syncSchedulesToElasticsearch();
        return ApiResponse.createSuccess(null);
    }

    @PostMapping("/v1/centers/sync-elasticsearch")
    public ApiResponse<Void> syncCenters() {
        elasticsearchSyncService.syncCentersToElasticsearch();
        return ApiResponse.createSuccess(null);
    }
}

package com.sparta.service.schedule.repository;

import com.sparta.service.schedule.entity.ScheduleSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ScheduleSearchRepository extends ElasticsearchRepository<ScheduleSearch, Long> {

}

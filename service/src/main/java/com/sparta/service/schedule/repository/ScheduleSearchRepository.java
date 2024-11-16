package com.sparta.service.schedule.repository;

import com.sparta.service.schedule.entity.ScheduleSearch;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ScheduleSearchRepository extends ElasticsearchRepository<ScheduleSearch, Long> {

    List<ScheduleSearch> findByUserIdAndYearAndMonthAndDay(Long userId, Integer year, Integer month,
            Integer day);

    List<ScheduleSearch> findByUserIdAndYearAndMonth(Long userId, Integer year, Integer month);
}

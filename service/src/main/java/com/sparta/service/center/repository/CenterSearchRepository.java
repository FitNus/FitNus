package com.sparta.service.center.repository;

import com.sparta.service.center.entity.CenterSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CenterSearchRepository extends ElasticsearchRepository<CenterSearch, Long> {

}

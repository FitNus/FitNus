package com.sparta.service.center.repository;

import com.sparta.service.center.entity.CenterSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CenterSearchRepository extends ElasticsearchRepository<CenterSearch, Long> {

}

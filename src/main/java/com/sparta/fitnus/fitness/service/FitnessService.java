package com.sparta.fitnus.fitness.service;

import com.sparta.fitnus.fitness.repository.FitnessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FitnessService {
    private final FitnessRepository fitnessRepository;
    

}

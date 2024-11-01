package com.sparta.modulecommon.search.service;

import com.sparta.modulecommon.center.repository.CenterRepository;
import com.sparta.modulecommon.club.repository.ClubRepository;
import com.sparta.modulecommon.search.dto.response.SearchCenterResponse;
import com.sparta.modulecommon.search.dto.response.SearchClubResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SearchServiceTest {

    @InjectMocks
    private SearchService searchService;

    @Mock
    private ClubRepository clubRepository;

    @Mock
    private CenterRepository centerRepository;

    @Test
    public void 테스트_클럽_검색() {
        // Given
        Long clubId = 1L;
        String clubName = "런닝 클럽";
        String clubInfo = "기분좋게 달려요";
        String place = "한강";
        LocalDateTime date = LocalDateTime.now(); // 현재 시간

        SearchClubResponse response = new SearchClubResponse(
                clubId, clubName, clubInfo, place, date);
        Page<SearchClubResponse> expectedPage = new PageImpl<>(Collections.singletonList(response),
                PageRequest.of(0, 10), 1);

        when(clubRepository.search(eq(clubName), eq(clubInfo), eq(place),
                ArgumentMatchers.any())).thenReturn(expectedPage);

        // When
        Page<SearchClubResponse> result = searchService.searchClubs(
                clubName, clubInfo, place, 1, 10);

        // Then
        assertEquals(expectedPage.getContent(), result.getContent());
        assertEquals(expectedPage.getTotalElements(), result.getTotalElements());
    }

    @Test
    public void 테스트_센터_검색() {
        // Given
        Long centerId = 1L;
        String centerName = "복싱 센터";
        String fitnessName = "복싱";

        SearchCenterResponse response = new SearchCenterResponse(centerId, centerName, fitnessName);
        Page<SearchCenterResponse> expectedPage = new PageImpl<>(
                Collections.singletonList(response), PageRequest.of(0, 10), 1);

        when(centerRepository.search(eq(centerName), eq(fitnessName),
                ArgumentMatchers.any())).thenReturn(expectedPage);

        // When
        Page<SearchCenterResponse> result = searchService.searchCenters(centerName, fitnessName, 1,
                10);

        // Then
        assertEquals(expectedPage.getContent(), result.getContent());
        assertEquals(expectedPage.getTotalElements(), result.getTotalElements());
    }
}

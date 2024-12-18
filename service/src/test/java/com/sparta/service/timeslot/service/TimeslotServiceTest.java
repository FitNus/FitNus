package com.sparta.service.timeslot.service;

import com.sparta.common.enums.UserRole;
import com.sparta.common.user.dto.AuthUser;
import com.sparta.service.center.entity.Center;
import com.sparta.service.center.service.CenterService;
import com.sparta.service.fitness.entity.Fitness;
import com.sparta.service.fitness.exception.AccessDeniedException;
import com.sparta.service.timeslot.dto.request.TimeslotRequest;
import com.sparta.service.timeslot.entity.Timeslot;
import com.sparta.service.timeslot.exception.TimeslotNotFoundException;
import com.sparta.service.timeslot.repository.TimeslotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeslotServiceTest {

    @Mock
    private TimeslotRepository timeslotRepository;

    @Mock
    private CenterService centerService;

    @InjectMocks
    private TimeslotService timeslotService;

    private Center center;
    private Fitness fitness;
    private final Long centerId = 1L;
    private final Long fitnessId = 2L;
    private final Long timeslotId = 3L;
    private final Long ownerId = 100L;


    @BeforeEach
    void setUp() {
        center = new Center();
        fitness = new Fitness();

        ReflectionTestUtils.setField(center, "id", centerId);
        ReflectionTestUtils.setField(center, "ownerId", ownerId);
        ReflectionTestUtils.setField(fitness, "id", fitnessId);
        ReflectionTestUtils.setField(fitness, "center", center);
    }

    @Test
    void createTimeslot_InvalidOwner_ThrowsAccessDeniedException() {
        // given
        AuthUser anotherUser = new AuthUser(200L, UserRole.OWNER, "other@example.com", "OtherUser");
        TimeslotRequest request = new TimeslotRequest(fitnessId, centerId, LocalDateTime.of(2024, 11, 1, 9, 0), LocalDateTime.of(2024, 11, 1, 10, 0), 50);

        given(centerService.getCenterId(centerId)).willReturn(center);

        // when & then
        assertThrows(AccessDeniedException.class, () -> timeslotService.createTimeslot(anotherUser, request));
        verify(timeslotRepository, never()).save(any(Timeslot.class));
    }

    @Test
    void getTimeslot_InvalidId_ThrowsTimeslotNotFoundException() {
        // given
        given(timeslotRepository.findById(timeslotId)).willReturn(Optional.empty());

        // when & then
        assertThrows(TimeslotNotFoundException.class, () -> timeslotService.getTimeslot(timeslotId));
        verify(timeslotRepository, times(1)).findById(timeslotId);
    }

    @Test
    void isValidTimeslot_ValidId_ReturnsTimeslot() {
        // given
        Timeslot timeslot = new Timeslot();
        given(timeslotRepository.findById(timeslotId)).willReturn(Optional.of(timeslot));

        // when
        Timeslot result = timeslotService.isValidTimeslot(timeslotId);

        // then
        assertNotNull(result);
        verify(timeslotRepository, times(1)).findById(timeslotId);
    }

    @Test
    void isValidTimeslot_InvalidId_ThrowsTimeslotNotFoundException() {
        // given
        given(timeslotRepository.findById(timeslotId)).willReturn(Optional.empty());

        // when & then
        assertThrows(TimeslotNotFoundException.class, () -> timeslotService.isValidTimeslot(timeslotId));
        verify(timeslotRepository, times(1)).findById(timeslotId);
    }
}

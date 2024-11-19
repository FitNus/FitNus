package com.sparta.service.fitness.service;

import com.sparta.common.dto.AuthUser;
import com.sparta.common.enums.UserRole;
import com.sparta.service.center.entity.Center;
import com.sparta.service.center.service.CenterService;
import com.sparta.service.fitness.dto.request.FitnessDeleteRequest;
import com.sparta.service.fitness.dto.request.FitnessRequest;
import com.sparta.service.fitness.dto.response.FitnessResponse;
import com.sparta.service.fitness.entity.Fitness;
import com.sparta.service.fitness.exception.AccessDeniedException;
import com.sparta.service.fitness.exception.FitnessNotFoundException;
import com.sparta.service.fitness.exception.FitnessgetAllAccessDeniedException;
import com.sparta.service.fitness.repository.FitnessRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FitnessServiceTest {

    @Mock
    private FitnessRepository fitnessRepository;

    @Mock
    private CenterService centerService;

    @InjectMocks
    private FitnessService fitnessService;

    private final Long fitnessId = 1L;
    private final Long centerId = 1L;
    private final Long ownerId = 100L;
    private AuthUser authUser;
    private Center center;

    @BeforeEach
    void setUp() {
        authUser = new AuthUser(ownerId, UserRole.OWNER, "test@example.com", "TestUser");
        center = new Center();
        ReflectionTestUtils.setField(center, "id", centerId);
        ReflectionTestUtils.setField(center, "ownerId", ownerId);
    }


    @Test
    void createFitness_InvalidOwner_ThrowsAccessDeniedException() {
        // given
        AuthUser anotherUser = new AuthUser(200L, UserRole.OWNER, "other@example.com", "OtherUser");
        FitnessRequest request = new FitnessRequest("Yoga Class", 5, centerId);

        given(centerService.getCenterId(centerId)).willReturn(center);

        // when & then
        assertThrows(FitnessgetAllAccessDeniedException.class, () ->
                fitnessService.createFitness(anotherUser, request)
        );
        verify(fitnessRepository, never()).save(any(Fitness.class));
    }

    @Test
    void getFitness_ValidId_ReturnsFitnessResponse() {
        // given
        Fitness fitness = new Fitness(new FitnessRequest("Yoga Class", 5, centerId), center);
        ReflectionTestUtils.setField(fitness, "id", fitnessId);

        given(fitnessRepository.findById(fitnessId)).willReturn(Optional.of(fitness));

        // when
        FitnessResponse response = fitnessService.getFitness(fitnessId);

        // then
        assertNotNull(response);
        verify(fitnessRepository, times(1)).findById(fitnessId);
    }

    @Test
    void getFitness_InvalidId_ThrowsFitnessNotFoundException() {
        // given
        given(fitnessRepository.findById(fitnessId)).willReturn(Optional.empty());

        // when & then
        assertThrows(FitnessNotFoundException.class, () -> fitnessService.getFitness(fitnessId));
        verify(fitnessRepository, times(1)).findById(fitnessId);
    }

    @Test
    void getAllFitness_InvalidOwner_ThrowsAccessDeniedException() {
        // given
        AuthUser anotherUser = new AuthUser(200L, UserRole.OWNER, "other@example.com", "OtherUser");

        given(centerService.getCenterId(centerId)).willReturn(center);

        // when & then
        assertThrows(FitnessgetAllAccessDeniedException.class, () -> fitnessService.getAllFitness(anotherUser, centerId));
        verify(fitnessRepository, never()).findAllByCenterId(centerId);
    }

    @Test
    void updateFitness_ValidOwner_UpdatesFitness() {
        // given
        FitnessRequest updateRequest = new FitnessRequest("Updated Class", 10, centerId);
        Fitness fitness = new Fitness(new FitnessRequest("Yoga Class", 5, centerId), center);

        given(fitnessRepository.findById(fitnessId)).willReturn(Optional.of(fitness));
        given(centerService.getCenterId(centerId)).willReturn(center);

        // when
        FitnessResponse response = fitnessService.updateFitness(authUser, fitnessId, updateRequest);

        // then
        assertNotNull(response);
        assertEquals("Updated Class", response.getFitnessName());
        assertEquals(10, response.getRequiredCoupon());
    }

    @Test
    void deleteFitness_ValidOwner_DeletesFitness() {
        // given
        FitnessDeleteRequest deleteRequest = new FitnessDeleteRequest(centerId);

        given(fitnessRepository.findById(fitnessId)).willReturn(Optional.of(new Fitness()));
        given(centerService.getCenterId(centerId)).willReturn(center);

        // when
        fitnessService.deleteFitness(authUser, fitnessId, deleteRequest);

        // then
        verify(fitnessRepository, times(1)).deleteById(fitnessId);
    }

    @Test
    void deleteFitness_InvalidOwner_ThrowsAccessDeniedException() {
        // given
        AuthUser anotherUser = new AuthUser(200L, UserRole.OWNER, "other@example.com", "OtherUser");
        FitnessDeleteRequest deleteRequest = new FitnessDeleteRequest(centerId);

        given(fitnessRepository.findById(fitnessId)).willReturn(Optional.of(new Fitness()));
        given(centerService.getCenterId(centerId)).willReturn(center);

        // when & then
        assertThrows(AccessDeniedException.class, () -> fitnessService.deleteFitness(anotherUser, fitnessId, deleteRequest));
        verify(fitnessRepository, never()).deleteById(fitnessId);
    }
}

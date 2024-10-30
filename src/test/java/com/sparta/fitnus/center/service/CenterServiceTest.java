package com.sparta.fitnus.center.service;

import com.sparta.fitnus.center.dto.request.CenterSaveRequest;
import com.sparta.fitnus.center.dto.request.CenterUpdateRequest;
import com.sparta.fitnus.center.dto.response.CenterResponse;
import com.sparta.fitnus.center.entity.Center;
import com.sparta.fitnus.center.exception.CenterAccessDeniedException;
import com.sparta.fitnus.center.exception.CenterNotFoundException;
import com.sparta.fitnus.center.repository.CenterRepository;
import com.sparta.fitnus.user.entity.AuthUser;
import com.sparta.fitnus.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//@ExtendWith(SpringExtension.class)
@SpringBootTest
class CenterServiceTest {

    @MockBean
    private CenterRepository centerRepository;

    @Autowired
    private CenterService centerService;

    private Center centerSpy;
    private AuthUser authUser;
    private final Long centerId = 1L; // 공통 ID 설정

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // AuthUser 및 CenterSaveRequest 생성
        authUser = new AuthUser(1L, UserRole.OWNER, "test@example.com", "TestUser");
        CenterSaveRequest saveRequest = new CenterSaveRequest("Test Center", 9, 18);
        // Center 객체를 spy로 감싸고 ID 반환값 설정
        centerSpy = spy(new Center(saveRequest, authUser));
        ReflectionTestUtils.setField(centerSpy, "id", 1L);
        doReturn(centerId).when(centerSpy).getId();
    }

    @Test
    void getCenter_ValidId_ReturnsCenterResponse() {
        // given
        when(centerRepository.findById(any())).thenReturn(Optional.of(centerSpy));

        // when
        CenterResponse response = centerService.getCenter(1L);

        // then
        assertNotNull(response);
        assertEquals(1, response.getId());
        verify(centerRepository, times(1)).findById(any());
    }

}

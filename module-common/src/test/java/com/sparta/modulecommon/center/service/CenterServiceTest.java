package com.sparta.modulecommon.center.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.sparta.modulecommon.center.dto.request.CenterSaveRequest;
import com.sparta.modulecommon.center.dto.request.CenterUpdateRequest;
import com.sparta.modulecommon.center.dto.response.CenterResponse;
import com.sparta.modulecommon.center.entity.Center;
import com.sparta.modulecommon.center.exception.CenterNotFoundException;
import com.sparta.modulecommon.center.repository.CenterRepository;
import com.sparta.modulecommon.user.entity.AuthUser;
import com.sparta.modulecommon.user.enums.UserRole;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CenterServiceTest {

    @Mock // any()는 mock에 넣자
    private CenterRepository centerRepository; // mock객체는 Proxy고, proxy객체는 final 사용x 그래서 private -o / private final -x

    @InjectMocks // InjectMock은 실제객체 가져오기 때문에, centerService에는 any()가 아닌, 실제 자원을 넣어줘야함.
    private CenterService centerService;

    private AuthUser authUser = new AuthUser(1L, UserRole.OWNER, "test@example.com", "TestUser");
    private CenterSaveRequest request = new CenterSaveRequest("Center-Name", "test", 9, 22);
    private CenterUpdateRequest updateRequest = new CenterUpdateRequest("Updated Center", 10, 23);
    private final Long centerId = 1L; // 공통 ID 설정
    private final Long ownerId = 100L;


    @Test
    void getCenter_ValidId_ReturnsCenterResponse() {
        // given: centerId로 조회할 Center 객체 생성 및 반환값 설정
        Center center = new Center(request, authUser); // 필요한 기본 정보만 설정된 Center 객체
        ReflectionTestUtils.setField(center, "id",
                1L); // CenterId는 autoincrement로 생성되는거니까, 테스트에선 내가 직접 setField로 집어넣어줘야 함.

        given(centerRepository.findCenterById(1L)).willReturn(center);

        // when: centerService.getCenter 호출
        CenterResponse response = centerService.getCenter(1L);

        // then: 반환된 CenterResponse 검증
        assertNotNull(response);
        assertEquals(1L, response.getId()); // 반환된 응답 ID가 요청한 ID와 일치하는지 검증
        verify(centerRepository, times(1)).findCenterById(1L); // 호출 횟수 검증
    }

    @Test
    void createCenter_ReturnsCenterResponse() {

        // given
        Center center = new Center(request, authUser);
        ReflectionTestUtils.setField(center, "id", 1L);
//        CenterResponse response = new CenterResponse(centerRepository.save(center));
        given(centerRepository.save(any(Center.class))).willReturn(center);
        // when - 해당 메서드 부르고, 매개변수 있다면 넣어주는 단계
        CenterResponse response = centerService.createCenter(authUser, request);
        // then - 여러가지 테스트 해보는 단계
        assertNotNull(response);
        assertEquals(1L, center.getId());
        assertEquals("Center-Name", center.getCenterName());
        assertEquals(9, center.getOpenTime());
        assertEquals(22, center.getCloseTime());

        verify(centerRepository, times(1)).save(any(Center.class));

    }

//    @Test
//    void updateCenter_ValidOwner_SuccessfulUpdate() {
//        // given
//        Center center = new Center(new CenterSaveRequest("Center-Name", 9, 22), authUser);
//        ReflectionTestUtils.setField(center, "id", 1L);
//
//        given(centerRepository.findCenterById(1L)).willReturn(center);
//
//        // when
//        CenterResponse response = centerService.updateCenter(authUser, 2L, updateRequest);
//
//        // then
//        assertNotNull(response);
//        assertEquals("Updated Center", response.getCenterName());
//        assertEquals(10, response.getOpenTime());
//        assertEquals(20, response.getCloseTime());
//        verify(centerRepository, times(1)).findCenterById(1L);
//    }
//    @Test
//    void isValidOwnerInCenter_ReturnOptional_Long() {
//        // given
//        given(centerRepository.findOwnerIdByCenterId(anyLong())).willReturn(Optional.of(1L));
//        // when
//        Long result = centerService.isValidOwnerInCenter(1L);
//        // then
//        assertNotNull(result);
//        verify(centerRepository, times(1)).findOwnerIdByCenterId(1L);
//    }

    @Test
    void isValidOwnerInCenter_ValidCenterId_ReturnsOwnerId() {
        // given
        given(centerRepository.findOwnerIdByCenterId(centerId)).willReturn(Optional.of(ownerId));

        // when
        Long result = centerService.isValidOwnerInCenter(centerId);

        // then
        assertNotNull(result);
        assertEquals(ownerId, result);
        verify(centerRepository, times(1)).findOwnerIdByCenterId(centerId);
    }

    @Test
    void isValidOwnerInCenter_InvalidCenterId_ThrowsCenterNotFoundException() {
        // given
        given(centerRepository.findOwnerIdByCenterId(centerId)).willReturn(Optional.empty());

        // when & then
        assertThrows(CenterNotFoundException.class,
                () -> centerService.isValidOwnerInCenter(centerId));
        verify(centerRepository, times(1)).findOwnerIdByCenterId(centerId);
    }

//    @Test
//    void deleteCenter_ValidOwner_DeletesCenter() {
//        // given
//        AuthUser authUser = new AuthUser(ownerId, UserRole.OWNER, "test@example.com", "TestUser");
//        given(centerService.isValidOwnerInCenter(centerId)).willReturn(ownerId);
//
//        // when
//        centerService.deleteCenter(authUser, centerId);
//
//        // then
//        verify(centerRepository, times(1)).deleteById(centerId);
//    }

//    @Test
//    void deleteCenter_InvalidOwner_ThrowsAccessDeniedException() {
//        // given
//        AuthUser authUser = new AuthUser(200L, UserRole.OWNER, "other@example.com", "OtherUser"); // 다른 사용자
//        given(centerService.isValidOwnerInCenter(centerId)).willReturn(ownerId);
//
//        // when & then
//        assertThrows(CenterAccessDeniedException.class, () ->
//                centerService.deleteCenter(authUser, centerId)
//        );
//        verify(centerRepository, never()).deleteById(centerId);
//    }
}

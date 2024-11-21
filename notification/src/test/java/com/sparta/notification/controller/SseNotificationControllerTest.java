package com.sparta.notification.controller;//package com.sparta.fitnus.ssenotification.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sparta.common.apipayload.ApiResponse;
import com.sparta.common.config.JwtSecurityFilter;
import com.sparta.common.enums.UserRole;
import com.sparta.common.user.dto.AuthUser;
import com.sparta.notification.dto.EventPayload;
import com.sparta.notification.service.NotificationService;
import com.sparta.notification.service.SseNotificationServiceImpl;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(
        controllers = SseNotificationController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = JwtSecurityFilter.class
                )
        }
)
class SseNotificationControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private SseNotificationServiceImpl sseNotificationServiceImpl;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private SseNotificationController sseNotificationController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Nested
    class subscribe {

        @Test
        void subscribe_성공() {
            // Given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            SseEmitter emitter = new SseEmitter();
            given(sseNotificationServiceImpl.subscribe(authUser.getId())).willReturn(emitter);

            // When
            SseEmitter result = sseNotificationController.subscribe(authUser);

            // Then
            verify(sseNotificationServiceImpl, times(1)).subscribe(authUser.getId());
            assertNotNull(result);
        }
    }

    @Nested
    class getUnreadNotifications {

        @Test
        void unread_성공() {
            // Given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            List<EventPayload> notifications = new ArrayList<>();
            Pageable pageable = PageRequest.of(0, 10);
            given(notificationService.getNotifications(authUser.getId(), "unread", pageable))
                .willReturn(notifications);

            // When
            ApiResponse<List<EventPayload>> response = sseNotificationController.getNotifications(
                authUser, "unread", 1, 10);

            // Then
            assertNotNull(response);
            verify(notificationService, times(1)).getNotifications(authUser.getId(), "unread", pageable);
        }
        }

        @Test
        void all_성공() {
            // Given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            List<EventPayload> notifications = new ArrayList<>();
            Pageable pageable = PageRequest.of(0, 10);
            given(notificationService.getNotifications(authUser.getId(), "all", pageable))
                .willReturn(notifications);

            // When
            ApiResponse<List<EventPayload>> response = sseNotificationController.getNotifications(
                authUser, "all", 1, 10);

            // Then
            assertNotNull(response);
            verify(notificationService, times(1)).getNotifications(authUser.getId(), "all", pageable);
        }
    }



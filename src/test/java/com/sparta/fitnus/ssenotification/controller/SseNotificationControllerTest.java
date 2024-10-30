package com.sparta.fitnus.ssenotification.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sparta.fitnus.config.JwtSecurityFilter;
import com.sparta.fitnus.ssenotification.service.SseNotificationServiceImpl;
import com.sparta.fitnus.user.entity.AuthUser;
import com.sparta.fitnus.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
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
    class markAsRead {

        @Test
        void markAsRead_성공() {
            //given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test", "test");

            //when
            sseNotificationController.markAsRead(authUser, 1L);

            //then
            verify(sseNotificationServiceImpl, times(1)).markAsRead(1L, 1L);
        }
    }
}
package com.sparta.user.kakao.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sparta.common.config.JwtSecurityFilter;
import com.sparta.common.config.JwtUtil;
import com.sparta.common.config.RedisUserService;
import com.sparta.user.kakao.service.KakaoAuthService;
import com.sparta.user.user.dto.response.AuthTokenResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(
        controllers = KakaoAuthController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = JwtSecurityFilter.class
                )
        }
)
class KakaoAuthControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private KakaoAuthService kakaoAuthservice;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private RedisUserService redisUserService;

    @Value("${kakao.client.id}")
    private String kakaoClientId;

    @Value("${kakao.client.redirect}")
    private String kakaoClientRedirect;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Nested
    class kakaoSignupLogin {

        @Test
        void kakaoSignupLogin_성공() throws Exception {
            //given

            //when
            ResultActions result = mockMvc.perform(get("/api/v1/auth/kakao/signup-login"));

            //then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    class kakaoCallback {

        @Test
        void kakaoCallback_성공() throws Exception {
            //given
            String accessToken = "testAccessToken";
            String refreshToken = "testRefreshToken";
            AuthTokenResponse authTokenResponse = new AuthTokenResponse(accessToken, refreshToken);

            given(kakaoAuthservice.handleKakaoAuth("testCode")).willReturn(authTokenResponse);

            //when
            mockMvc.perform(get("/api/v1/auth/kakao/callback")
                            .param("code", "testCode")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login-success.html"));

            //then
            verify(jwtUtil).setTokenCookie(any(HttpServletResponse.class), eq(accessToken));
            verify(jwtUtil).setRefreshTokenCookie(any(HttpServletResponse.class), eq(refreshToken));
        }
    }
}
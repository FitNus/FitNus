package com.sparta.fitnus.kakao.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sparta.fitnus.config.JwtSecurityFilter;
import com.sparta.fitnus.config.JwtUtil;
import com.sparta.fitnus.kakao.service.KakaoAuthService;
import com.sparta.fitnus.user.entity.User;
import com.sparta.fitnus.user.service.RedisUserService;
import com.sparta.fitnus.user.service.UserService;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private UserService userService;

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
            User mockUser = new User();
            ReflectionTestUtils.setField(mockUser, "id", 1L);
            String accessToken = "mockAccessToken";

            given(userService.createAccessToken(mockUser)).willReturn(accessToken);

            //when
            ResultActions result = mockMvc.perform(get("/api/v1/auth/kakao/callback")
                    .param("code", "testCode"));

            //then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    class kakaoSignup {

        @Test
        void kakaoSignup_성공() throws Exception {
            //given
            String accessToken = "mockAccessToken";
            String email = "test@test.com";

            given(kakaoAuthservice.getEmailFromKakao(accessToken)).willReturn(email);

            //when
            ResultActions result = mockMvc.perform(post("/api/v1/auth/kakao/signup")
                    .header("Authorization", accessToken)
                    .contentType(MediaType.APPLICATION_JSON));

            //then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    class kakaoLogin {

        @Test
        void kakaoLogin_성공() throws Exception {
            //given
            User mockUser = new User();
            ReflectionTestUtils.setField(mockUser, "id", 1L);
            String accessToken = "mockAccessToken";
            String refreshToken = "mockRefreshToken";
            String email = "test@test.com";

            given(kakaoAuthservice.getEmailFromKakao(accessToken)).willReturn(email);
            given(userService.getUserFromEmail(email)).willReturn(mockUser);
            given(kakaoAuthservice.createAccessToken(mockUser)).willReturn(accessToken);
            given(kakaoAuthservice.createRefreshToken(mockUser)).willReturn(refreshToken);

            //when
            ResultActions result = mockMvc.perform(post("/api/v1/auth/kakao/login")
                    .header("Authorization", accessToken)
                    .contentType(MediaType.APPLICATION_JSON));

            //then
            result.andExpect(status().isOk());
        }
    }
}
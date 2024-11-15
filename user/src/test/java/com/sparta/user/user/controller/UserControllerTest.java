package com.sparta.user.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sparta.user.config.JwtSecurityFilter;
import com.sparta.user.config.JwtUtil;
import com.sparta.user.user.dto.request.ChangePasswordRequest;
import com.sparta.user.user.dto.request.UserRequest;
import com.sparta.user.user.dto.response.AuthTokenResponse;
import com.sparta.user.user.service.CouponService;
import com.sparta.user.user.service.UserService;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(
        controllers = UserController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = JwtSecurityFilter.class
                )
        }
)
class UserControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private CouponService CouponService;

    @MockBean
    private JwtUtil jwtUtil;

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
    class signup {

        @Test
        void signup_성공() throws Exception {
            //given
            UserRequest userRequest = new UserRequest();

            //when
            ResultActions result = mockMvc.perform(post("/api/v1/auth/signup")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(userRequest)));

            //then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    class login {

        @Test
        void login_성공() throws Exception {
            //given
            UserRequest userRequest = new UserRequest();
            String accessToken = "mockAccessToken";
            String refreshToken = "mockRefreshToken";
            AuthTokenResponse authTokenResponse = new AuthTokenResponse(accessToken, refreshToken);

            given(userService.login(any())).willReturn(authTokenResponse);

            //when
            ResultActions result = mockMvc.perform(post("/api/v1/auth/login")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(userRequest)));

            //then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    class logout {

        @Test
        void logout_성공() throws Exception {
            //given

            //when
            ResultActions result = mockMvc.perform(post("/api/v1/auth/logout"));

            //then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    class changePassword {

        @Test
        void changePassword_성공() throws Exception {
            //given
            ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();

            //when
            ResultActions result = mockMvc.perform(post("/api/v1/user/{userId}/change-password", 1L)
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(changePasswordRequest)));

            //then
            result.andExpect(status().isOk());
        }

        @Nested
        class deleteUser {

            @Test
            void deleteUser_성공() throws Exception {
                //given
                UserRequest userRequest = new UserRequest();

                //when
                ResultActions result = mockMvc.perform(delete("/api/v1/user/{userId}/delete", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userRequest)));

                //then
                result.andExpect(status().isOk());
            }
        }

        @Nested
        class deactivateUser {

            @Test
            void deactivateUser_성공() throws Exception {
                //given

                //when
                ResultActions result = mockMvc.perform(put("/api/v1/admin/{userId}/deactivate", 1L));

                //then
                result.andExpect(status().isOk());
            }
        }
    }
}
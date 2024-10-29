package com.sparta.fitnus.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sparta.fitnus.config.JwtSecurityFilter;
import com.sparta.fitnus.user.dto.request.ProfileBioRequest;
import com.sparta.fitnus.user.dto.request.ProfileNicknameRequest;
import com.sparta.fitnus.user.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(
        controllers = ProfileController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = JwtSecurityFilter.class
                )
        }
)
class ProfileControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private ProfileService profileService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Nested
    class attachFile {

        @Test
        void attachFile_성공() throws Exception {
            //given

            //when
            ResultActions result = mockMvc.perform(post("/api/v1/users/images")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .param("file", "testFile.jpg"));

            //then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    class deleteFile {

        @Test
        void deleteFile_성공() throws Exception {
            //given

            //when
            ResultActions result = mockMvc.perform(delete("/api/v1/users/images"));

            //then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    class getUser {

        @Test
        void getUser_성공() throws Exception {
            //given

            //when
            ResultActions result = mockMvc.perform(get("/api/v1/users/{id}", 1L));

            //then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    class updateBio {

        @Test
        void updateBio_성공() throws Exception {
            //given
            ProfileBioRequest profileBioRequest = new ProfileBioRequest();

            //when
            ResultActions result = mockMvc.perform(put("/api/v1/users/bio")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(profileBioRequest)));

            //then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    class updateNickname {

        @Test
        void updateNickname_성공() throws Exception {
            //given
            ProfileNicknameRequest profileNicknameRequest = new ProfileNicknameRequest();

            //when
            ResultActions result = mockMvc.perform(patch("/api/v1/users/nickname")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(profileNicknameRequest)));

            //then
            result.andExpect(status().isOk());
        }
    }
}
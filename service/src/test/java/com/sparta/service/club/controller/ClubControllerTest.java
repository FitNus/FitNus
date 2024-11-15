package com.sparta.service.club.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sparta.common.config.JwtSecurityFilter;
import com.sparta.service.club.dto.request.ClubRequest;
import com.sparta.service.club.service.ClubService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(
        controllers = ClubController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = JwtSecurityFilter.class
                )
        }
)
class ClubControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private ClubService clubService;

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
    class createClub {

        @Test
        void 모임_생성_성공() throws Exception {
            //given
            ClubRequest clubRequest = new ClubRequest();

            //when
            ResultActions result = mockMvc.perform(post("/api/v1/clubs")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(clubRequest)));

            //then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    class updateClub {

        @Test
        void 모임_수정_성공() throws Exception {
            //given
            ClubRequest clubRequest = new ClubRequest();

            //when
            ResultActions result = mockMvc.perform(put("/api/v1/clubs/{id}", 1L)
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(clubRequest)));

            //then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    class getClub {

        @Test
        void 모임_단건조회_성공() throws Exception {
            //given

            //when
            ResultActions result = mockMvc.perform(get("/api/v1/clubs/{id}", 1L));

            //then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    class deleteClub {

        @Test
        void 모임_삭제_성공() throws Exception {
            //given

            //when
            ResultActions result = mockMvc.perform(delete("/api/v1/clubs/{id}", 1L));

            //then
            result.andExpect(status().isOk());
        }
    }
}
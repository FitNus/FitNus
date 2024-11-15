package com.sparta.service.center.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sparta.service.center.dto.request.CenterSaveRequest;
import com.sparta.service.center.dto.request.CenterUpdateRequest;
import com.sparta.service.center.service.CenterService;
import com.sparta.user.config.JwtSecurityFilter;
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
        controllers = CenterController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = JwtSecurityFilter.class
                )
        }
)
class CenterControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private CenterService centerService;

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
    class addCenter {

        @Test
        void addCenter_성공() throws Exception {
            //given
            CenterSaveRequest centerSaveRequest = new CenterSaveRequest();

            //when
            ResultActions result = mockMvc.perform(post("/api/v1/centers")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(centerSaveRequest)));

            //then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    class updateCenter {

        @Test
        void updateCenter_성공() throws Exception {
            //given
            CenterUpdateRequest centerUpdateRequest = new CenterUpdateRequest();

            //when
            ResultActions result = mockMvc.perform(patch("/api/v1/centers/{id}", 1L)
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(centerUpdateRequest)));

            //then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    class deleteCenter {

        @Test
        void deleteCenter_성공() throws Exception {
            //given

            //when
            ResultActions result = mockMvc.perform(delete("/api/v1/centers/{id}", 1L));

            //then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    class getCenter {

        @Test
        void getCenter_성공() throws Exception {
            //given

            //when
            ResultActions result = mockMvc.perform(get("/api/v1/centers/{id}", 1L));

            //then
            result.andExpect(status().isOk());
        }
    }
}
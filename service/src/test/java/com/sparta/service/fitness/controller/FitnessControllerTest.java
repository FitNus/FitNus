package com.sparta.service.fitness.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sparta.common.config.JwtSecurityFilter;
import com.sparta.service.fitness.dto.request.FitnessDeleteRequest;
import com.sparta.service.fitness.dto.request.FitnessGetAllRequest;
import com.sparta.service.fitness.dto.request.FitnessRequest;
import com.sparta.service.fitness.service.FitnessService;
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
        controllers = FitnessController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = JwtSecurityFilter.class
                )
        }
)
class FitnessControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private FitnessService fitnessService;

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
    class addFitness {

        @Test
        void addFitness_성공() throws Exception {
            //given
            FitnessRequest fitnessRequest = new FitnessRequest();

            //when
            ResultActions result = mockMvc.perform(post("/api/v1/fitness")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(fitnessRequest)));

            //then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    class getFitness {

        @Test
        void getFitness_성공() throws Exception {
            //given

            //when
            ResultActions result = mockMvc.perform(get("/api/v1/fitness/{id}", 1L));

            //then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    class getAllFitness {

        @Test
        void getAllFitness_성공() throws Exception {
            //given
            FitnessGetAllRequest fitnessGetAllRequest = new FitnessGetAllRequest();

            //when
            ResultActions result = mockMvc.perform(get("/api/v1/fitness")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(fitnessGetAllRequest)));

            //then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    class updateFitness {

        @Test
        void updateFitness_성공() throws Exception {
            //given
            FitnessRequest fitnessRequest = new FitnessRequest();

            //when
            ResultActions result = mockMvc.perform(patch("/api/v1/fitness/{id}", 1L)
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(fitnessRequest)));

            //then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    class deleteFitness {

        @Test
        void deleteFitness_성공() throws Exception {
            //given
            FitnessDeleteRequest fitnessDeleteRequest = new FitnessDeleteRequest();

            //when
            ResultActions result = mockMvc.perform(delete("/api/v1/fitness/{id}", 1L)
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(fitnessDeleteRequest)));

            //then
            result.andExpect(status().isOk());
        }
    }
}
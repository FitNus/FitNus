package com.sparta.fitnus.timeslot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sparta.fitnus.config.JwtSecurityFilter;
import com.sparta.fitnus.timeslot.dto.request.TimeslotDeleteRequest;
import com.sparta.fitnus.timeslot.dto.request.TimeslotRequest;
import com.sparta.fitnus.timeslot.service.TimeslotService;
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
        controllers = TimeslotController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = JwtSecurityFilter.class
                )
        }
)
class TimeslotControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private TimeslotService timeslotService;

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
    class createTimeslot {

        @Test
        void createTimeslot_성공() throws Exception {
            //given
            TimeslotRequest timeslotRequest = new TimeslotRequest();

            //when
            ResultActions result = mockMvc.perform(post("/api/v1/timeslots")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(timeslotRequest)));

            //then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    class getTimeslot {

        @Test
        void getTimeslot_성공() throws Exception {
            //given

            //when
            ResultActions result = mockMvc.perform(get("/api/v1/timeslots/{id}", 1L));

            //then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    class getAllTimeslot {

        @Test
        void getAllTimeslot_성공() throws Exception {
            //given

            //when
            ResultActions result = mockMvc.perform(get("/api/v1/timeslots"));

            //then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    class deleteTimeslot {

        @Test
        void deleteTimeslot_성공() throws Exception {
            //given
            TimeslotDeleteRequest timeslotDeleteRequest = new TimeslotDeleteRequest();

            //when
            ResultActions result = mockMvc.perform(delete("/api/v1/timeslots/{id}", 1L)
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(timeslotDeleteRequest)));

            //then
            result.andExpect(status().isOk());
        }
    }
}
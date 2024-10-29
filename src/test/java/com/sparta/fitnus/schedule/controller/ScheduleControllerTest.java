package com.sparta.fitnus.schedule.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sparta.fitnus.config.JwtSecurityFilter;
import com.sparta.fitnus.schedule.dto.request.ScheduleRequest;
import com.sparta.fitnus.schedule.service.ScheduleService;
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
        controllers = ScheduleController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = JwtSecurityFilter.class
                )
        }
)
class ScheduleControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private ScheduleService scheduleService;

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
    class createSchedule {

        @Test
        void 일정_생성_성공() throws Exception {
            //given
            ScheduleRequest scheduleRequest = new ScheduleRequest();

            //when
            ResultActions result = mockMvc.perform(post("/api/v1/schedules")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(scheduleRequest)));

            //then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    class updateSchedule {

        @Test
        void 일정_수정_성공() throws Exception {
            //given
            ScheduleRequest scheduleRequest = new ScheduleRequest();

            //when
            ResultActions result = mockMvc.perform(put("/api/v1/schedules/{id}", 1L)
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(scheduleRequest)));

            //then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    class deleteSchedule {

        @Test
        void 일정_삭제_성공() throws Exception {
            //given

            //when
            ResultActions result = mockMvc.perform(delete("/api/v1/schedules/{id}", 1L));

            //then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    class getScheduleList {

        @Test
        void 일정_조회_성공() throws Exception {
            //given

            //when
            ResultActions result = mockMvc.perform(get("/api/v1/schedules"));

            //then
            result.andExpect(status().isOk());
        }
    }
}
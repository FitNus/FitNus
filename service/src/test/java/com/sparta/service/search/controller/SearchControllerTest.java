package com.sparta.service.search.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sparta.service.search.service.SearchService;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(
        controllers = SearchController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = JwtSecurityFilter.class
                )
        }
)
class SearchControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private SearchService searchService;

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
    class searchClubs {

        @Test
        void searchClubs_성공() throws Exception {
            //given

            //when
            ResultActions result = mockMvc.perform(get("/api/v1/search/clubs")
                    .contentType("application/json")
                    .param("clubName", "testClub")
                    .param("clubInfo", "testInfo")
                    .param("place", "testPlace")
                    .param("page", "1")
                    .param("size", "10")
                    .contentType(MediaType.APPLICATION_JSON));

            //then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    class searchCenters {

        @Test
        void searchCenters_성공() throws Exception {
            //given

            //when
            ResultActions result = mockMvc.perform(get("/api/v1/search/centers")
                    .contentType("application/json")
                    .param("centerName", "testCenter")
                    .param("fitnessName", "testFitness")
                    .param("userLat", "1.23")
                    .param("userLon", "2.23")
                    .param("radius", "3.23")
                    .param("page", "1")
                    .param("size", "10")
                    .contentType(MediaType.APPLICATION_JSON));

            //then
            result.andExpect(status().isOk());
        }
    }
}
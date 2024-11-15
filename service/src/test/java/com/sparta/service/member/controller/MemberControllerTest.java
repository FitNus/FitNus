package com.sparta.service.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sparta.common.config.JwtSecurityFilter;
import com.sparta.service.member.dto.request.MemberDeportRequest;
import com.sparta.service.member.dto.request.MemberRequest;
import com.sparta.service.member.service.MemberService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = MemberController.class, excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtSecurityFilter.class)})
class MemberControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Nested
    class getMemberList {

        @Test
        void 멤버_목록_조회_성공() throws Exception {
            //given
            MemberRequest memberRequest = new MemberRequest();

            //when
            ResultActions result = mockMvc.perform(get("/api/v1/members").contentType("application/json").content(objectMapper.writeValueAsString(memberRequest)));

            //then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    class withdrawMember {

        @Test
        void 멤버_탈퇴_성공() throws Exception {
            //given
            MemberRequest memberRequest = new MemberRequest();

            //when
            ResultActions result = mockMvc.perform(delete("/api/v1/members/withdraw").contentType("application/json").content(objectMapper.writeValueAsString(memberRequest)));

            //then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    class deportMember {

        @Test
        void 멤버_추방_성공() throws Exception {
            //given
            MemberDeportRequest memberDeportRequest = new MemberDeportRequest();

            //when
            ResultActions result = mockMvc.perform(delete("/api/v1/members/deport").contentType("application/json").content(objectMapper.writeValueAsString(memberDeportRequest)));

            //then
            result.andExpect(status().isOk());
        }
    }
}
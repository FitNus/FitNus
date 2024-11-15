package com.sparta.service.applicant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sparta.service.applicant.service.MemberApplicantService;
import com.sparta.service.member.dto.request.MemberAcceptRequest;
import com.sparta.service.member.dto.request.MemberRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(
        controllers = MemberApplicantController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = JwtSecurityFilter.class
                )
        }
)
class MemberApplicantControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private MemberApplicantService memberApplicantService;

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
    class createMemberApplicant {

        @Test
        void 멤버_가입신청_성공() throws Exception {
            //given
            MemberRequest memberRequest = new MemberRequest();

            //when
            ResultActions result = mockMvc.perform(post("/api/v1/member-applicants")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(memberRequest)));

            //then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    class acceptMemberApplicant {

        @Test
        void 멤버_가입신청_수락_성공() throws Exception {
            //given
            MemberAcceptRequest memberAcceptRequest = new MemberAcceptRequest();

            //when
            ResultActions result = mockMvc.perform(post("/api/v1/member-applicants/accept")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(memberAcceptRequest)));

            //then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    class rejectMemberApplicant {

        @Test
        void 멤버_가입신청_거절_성공() throws Exception {
            //given
            MemberAcceptRequest memberAcceptRequest = new MemberAcceptRequest();

            //when
            ResultActions result = mockMvc.perform(post("/api/v1/member-applicants/reject")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(memberAcceptRequest)));

            //then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    class getMemberApplicantList {

        @Test
        void 멤버_가입신청목록_조회_성공() throws Exception {
            //given
            MemberRequest memberRequest = new MemberRequest();

            //when
            ResultActions result = mockMvc.perform(get("/api/v1/member-applicants")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(memberRequest)));

            //then
            result.andExpect(status().isOk());
        }
    }
}
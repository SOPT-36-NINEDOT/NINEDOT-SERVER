package org.sopt36.ninedotserver.user.v1.controller;

import org.junit.jupiter.api.Test;
import org.sopt36.ninedotserver.auth.adapter.out.jwt.JwtProvider;
import org.sopt36.ninedotserver.auth.model.OnboardingPage;
import org.sopt36.ninedotserver.auth.port.in.ResolvePrincipalByTokenUsecase;
import org.sopt36.ninedotserver.global.security.JsonAuthenticationEntryPoint;
import org.sopt36.ninedotserver.global.security.JwtAuthenticationFactory;
import org.sopt36.ninedotserver.global.security.WithMockPrincipal;
import org.sopt36.ninedotserver.user.dto.query.UserInfoQuery;
import org.sopt36.ninedotserver.user.dto.result.UserInfoResult;
import org.sopt36.ninedotserver.user.port.in.GetUserInfoUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetUserInfoUseCase getUserInfoUseCase;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private ResolvePrincipalByTokenUsecase resolvePrincipalByTokenUsecase;

    @MockBean
    private JwtAuthenticationFactory jwtAuthenticationFactory;

    @MockBean
    private JsonAuthenticationEntryPoint jsonAuthenticationEntryPoint;

    @Test
    @WithMockPrincipal
    void 유저정보를_성공적으로_조회한다() throws Exception {
        // given
        UserInfoResult result = new UserInfoResult(
                1L,
                "홍길동",
                "test@example.com",
                "http://image.png",
                false,
                OnboardingPage.CORE_GOAL
        );
        given(getUserInfoUseCase.execute(any(UserInfoQuery.class))).willReturn(result);

        // when & then
        mockMvc.perform(get("/api/v1/users/info")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("유저 정보를 성공적으로 조회했습니다."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("홍길동"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.profileImageUrl").value("http://image.png"))
                .andExpect(jsonPath("$.data.onboardingCompleted").value(false))
                .andExpect(jsonPath("$.data.nextPage").value("CORE_GOAL"));
    }
}

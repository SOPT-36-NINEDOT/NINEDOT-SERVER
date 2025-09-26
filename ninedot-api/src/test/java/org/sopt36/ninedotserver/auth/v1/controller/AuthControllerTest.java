package org.sopt36.ninedotserver.auth.v1.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sopt36.ninedotserver.auth.adapter.out.jwt.JwtProvider;
import org.sopt36.ninedotserver.auth.dto.result.AuthResult;
import org.sopt36.ninedotserver.auth.dto.result.LoginResult;
import org.sopt36.ninedotserver.auth.dto.result.SignupResult;
import org.sopt36.ninedotserver.auth.model.ProviderType;
import org.sopt36.ninedotserver.auth.port.in.LoginOrSignupWithGoogleCodeUsecase;
import org.sopt36.ninedotserver.auth.service.AuthService;
import org.sopt36.ninedotserver.auth.v1.dto.request.GoogleAuthCodeRequest;
import org.sopt36.ninedotserver.global.web.CookieWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private LoginOrSignupWithGoogleCodeUsecase loginOrSignupWithGoogleCodeUsecase;

    @MockBean
    private CookieWriter cookieWriter;

    @MockBean
    private JwtProvider jwtProvider;

    @Nested
    @DisplayName("구글 소셜 인증 API (/api/v1/auth/oauth2/google/callback)")
    class GoogleCallbackTest {

        private final GoogleAuthCodeRequest body = new GoogleAuthCodeRequest("AUTH_CODE_123");
        private final String clientRedirectUri = "myapp://oauth2/callback";

        @Test
        @DisplayName("로그인 성공 시 200 OK를 반환하고 쿠키를 설정한다")
        void googleCallback_login_with_cookie_success() throws Exception {
            // given
            AuthResult loginResult = Mockito.mock(LoginResult.class);
            when(loginResult.refreshToken()).thenReturn(Optional.of("dummy-refresh-token"));
            when(loginOrSignupWithGoogleCodeUsecase.execute(any())).thenReturn(loginResult);

            // when & then
            mockMvc.perform(
                    post("/api/v1/auth/oauth2/google/callback")
                        .queryParam("redirect_uri", clientRedirectUri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                )
                .andExpect(status().isOk());

            // then
            verify(loginOrSignupWithGoogleCodeUsecase, times(1)).execute(any());
            verify(cookieWriter, times(1)).write(any(), any());
        }

        @Test
        @DisplayName("회원가입 성공 시 200 OK를 반환한다")
        void googleCallback_signup_success() throws Exception {
            // given
            SignupResult signupResult = Mockito.mock(SignupResult.class);
            when(signupResult.refreshToken()).thenReturn(Optional.empty());

            when(signupResult.provider()).thenReturn(ProviderType.GOOGLE);

            when(loginOrSignupWithGoogleCodeUsecase.execute(any())).thenReturn(signupResult);

            // when & then
            mockMvc.perform(
                    post("/api/v1/auth/oauth2/google/callback")
                        .queryParam("redirect_uri", clientRedirectUri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                )
                .andExpect(status().isOk());

            // then
            verify(loginOrSignupWithGoogleCodeUsecase, times(1)).execute(any());
            verify(cookieWriter, times(0)).write(any(), any());
        }
    }
}


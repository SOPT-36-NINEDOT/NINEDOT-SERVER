package org.sopt36.ninedotserver.auth.adapter.in.web.v1;

import static org.sopt36.ninedotserver.auth.adapter.in.web.v1.message.AuthMessage.ACCESS_TOKEN_REFRESH_SUCCESS;
import static org.sopt36.ninedotserver.auth.adapter.in.web.v1.message.AuthMessage.LOGIN_SIGNUP_SUCCESS;
import static org.sopt36.ninedotserver.auth.adapter.in.web.v1.message.AuthMessage.REFRESH_TOKEN_DELETED;
import static org.sopt36.ninedotserver.auth.adapter.in.web.v1.message.AuthMessage.SIGNUP_SUCCESS;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.auth.adapter.in.web.support.CookieWriter;
import org.sopt36.ninedotserver.auth.dto.request.SignupServiceRequest;
import org.sopt36.ninedotserver.auth.usecase.AuthService;
import org.sopt36.ninedotserver.auth.dto.response.LoginOrSignupResponse;
import org.sopt36.ninedotserver.auth.dto.response.NewAccessTokenResponse;
import org.sopt36.ninedotserver.auth.dto.response.SignupResponse;
import org.sopt36.ninedotserver.auth.adapter.in.web.v1.dto.request.GoogleAuthCodeRequest;
import org.sopt36.ninedotserver.auth.adapter.in.web.v1.dto.request.SignupRequest;
import org.sopt36.ninedotserver.auth.adapter.in.web.v1.mapper.AuthRequestMapper;
import org.sopt36.ninedotserver.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;
    private final CookieWriter cookieWriter;

    @PostMapping("/oauth2/google/callback")
    // TODO) Response 타입 명시적으로 바꾸기
    public ResponseEntity<ApiResponse<LoginOrSignupResponse<?>, Void>> googleCallback(
        @Valid @RequestBody GoogleAuthCodeRequest request,
        @RequestParam(value = "redirect_uri", required = false) String clientRedirectUri,
        HttpServletResponse response
    ) {
        LoginOrSignupResponse<?> giveback = authService.loginOrSignupWithCode(
            request.code(),
            clientRedirectUri
        );
        return ResponseEntity.ok(ApiResponse.ok(LOGIN_SIGNUP_SUCCESS, giveback));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<NewAccessTokenResponse, Void>> tokenRefresh(
        @CookieValue("refreshToken") String refreshToken,
        HttpServletResponse response
    ) {
        NewAccessTokenResponse newAccessTokenResponse = authService.createNewAccessToken(
            refreshToken
        );
        return ResponseEntity.ok(
            ApiResponse.ok(ACCESS_TOKEN_REFRESH_SUCCESS, newAccessTokenResponse)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void, Void>> deleteRefreshToken() {
        authService.deleteRefreshToken();
        return ResponseEntity.ok(ApiResponse.ok(REFRESH_TOKEN_DELETED));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse, Void>> registerUser(
        @RequestBody @Valid SignupRequest request,
        HttpServletResponse response
    ) {
        SignupServiceRequest serviceRequest = AuthRequestMapper.toSignupServiceRequest(request);
        SignupResponse signupResponse = authService.registerUser(serviceRequest);
        return ResponseEntity.ok(ApiResponse.ok(SIGNUP_SUCCESS, signupResponse));
    }
}

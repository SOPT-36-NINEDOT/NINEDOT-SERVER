package org.sopt36.ninedotserver.auth.v1;

import static org.sopt36.ninedotserver.auth.v1.message.AuthMessage.ACCESS_TOKEN_REFRESH_SUCCESS;
import static org.sopt36.ninedotserver.auth.v1.message.AuthMessage.LOGIN_SIGNUP_SUCCESS;
import static org.sopt36.ninedotserver.auth.v1.message.AuthMessage.REFRESH_TOKEN_DELETED;
import static org.sopt36.ninedotserver.auth.v1.message.AuthMessage.SIGNUP_SUCCESS;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.auth.usecase.AuthService;
import org.sopt36.ninedotserver.auth.dto.request.GoogleAuthCodeRequest;
import org.sopt36.ninedotserver.auth.dto.request.SignupRequest;
import org.sopt36.ninedotserver.auth.dto.response.LoginOrSignupResponse;
import org.sopt36.ninedotserver.auth.dto.response.NewAccessTokenResponse;
import org.sopt36.ninedotserver.auth.dto.response.SignupResponse;
import org.sopt36.ninedotserver.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/oauth2/google/callback")
    public ResponseEntity<ApiResponse<LoginOrSignupResponse<?>, Void>> googleCallback(
        @Valid @RequestBody GoogleAuthCodeRequest request,
        @RequestParam(value = "redirect_uri", required = false) String clientRedirectUri,
        HttpServletResponse response
    ) {
        LoginOrSignupResponse<?> giveback = authService.loginOrSignupWithCode(
            request.code(),
            clientRedirectUri,
            response
        );
        return ResponseEntity.ok(ApiResponse.ok(LOGIN_SIGNUP_SUCCESS, giveback));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<ApiResponse<NewAccessTokenResponse, Void>> tokenRefresh(
        @CookieValue("refreshToken") String refreshToken,
        HttpServletResponse response
    ) {
        NewAccessTokenResponse newAccessTokenResponse = authService.createNewAccessToken(
            refreshToken, response);
        return ResponseEntity.ok(
            ApiResponse.ok(ACCESS_TOKEN_REFRESH_SUCCESS, newAccessTokenResponse));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<ApiResponse<Void, Void>> deleteRefreshToken() {
        authService.deleteRefreshToken();
        return ResponseEntity.ok(ApiResponse.ok(REFRESH_TOKEN_DELETED));
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<ApiResponse<SignupResponse, Void>> registerUser(
        @RequestBody @Valid SignupRequest request,
        HttpServletResponse response
    ) {
        SignupResponse signupResponse = authService.registerUser(request, response);
        return ResponseEntity.ok(ApiResponse.ok(SIGNUP_SUCCESS, signupResponse));
    }
}

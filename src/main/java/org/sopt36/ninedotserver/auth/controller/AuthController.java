package org.sopt36.ninedotserver.auth.controller;

import static org.sopt36.ninedotserver.auth.controller.message.AuthMessage.LOGIN_SIGNUP_SUCCESS;
import static org.sopt36.ninedotserver.auth.controller.message.AuthMessage.REFRESH_TOKEN_DELETED;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.auth.dto.request.GoogleAuthCodeRequest;
import org.sopt36.ninedotserver.auth.dto.response.LoginOrSignupResponse;
import org.sopt36.ninedotserver.auth.service.AuthService;
import org.sopt36.ninedotserver.global.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/oauth2/google/callback")
    public ResponseEntity<ApiResponse<LoginOrSignupResponse<?>, Void>> googleCallback(
        @Valid @RequestBody GoogleAuthCodeRequest request,
        HttpServletResponse response) {
        LoginOrSignupResponse<?> giveback = authService.loginOrSignupWithCode(request.code(),
            response);
        return ResponseEntity.ok(ApiResponse.ok(LOGIN_SIGNUP_SUCCESS, giveback));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<ApiResponse<Void, Void>> deleteRefreshToken() {
        authService.deleteRefreshToken();
        return ResponseEntity.ok(ApiResponse.ok(REFRESH_TOKEN_DELETED));
    }
}

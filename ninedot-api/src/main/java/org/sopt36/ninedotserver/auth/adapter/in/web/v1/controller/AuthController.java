package org.sopt36.ninedotserver.auth.adapter.in.web.v1.controller;

import static org.sopt36.ninedotserver.auth.adapter.in.web.v1.message.AuthMessage.ACCESS_TOKEN_REFRESH_SUCCESS;
import static org.sopt36.ninedotserver.auth.adapter.in.web.v1.message.AuthMessage.LOGIN_SIGNUP_SUCCESS;
import static org.sopt36.ninedotserver.auth.adapter.in.web.v1.message.AuthMessage.REFRESH_TOKEN_DELETED;
import static org.sopt36.ninedotserver.auth.adapter.in.web.v1.message.AuthMessage.SIGNUP_SUCCESS;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.auth.adapter.in.web.support.CookieWriter;
import org.sopt36.ninedotserver.auth.adapter.in.web.v1.dto.request.GoogleAuthCodeRequest;
import org.sopt36.ninedotserver.auth.adapter.in.web.v1.dto.request.SignupRequest;
import org.sopt36.ninedotserver.auth.adapter.in.web.v1.dto.response.AuthResponse;
import org.sopt36.ninedotserver.auth.adapter.in.web.v1.mapper.AuthRequestMapper;
import org.sopt36.ninedotserver.auth.adapter.in.web.v1.mapper.AuthResponseMapper;
import org.sopt36.ninedotserver.auth.dto.command.GoogleLoginCommand;
import org.sopt36.ninedotserver.auth.dto.command.SignupCommand;
import org.sopt36.ninedotserver.auth.dto.response.NewAccessTokenResult;
import org.sopt36.ninedotserver.auth.dto.response.SignupResult;
import org.sopt36.ninedotserver.auth.dto.result.AuthResult;
import org.sopt36.ninedotserver.auth.port.in.LoginOrSignupWithGoogleCodeUsecase;
import org.sopt36.ninedotserver.auth.service.AuthService;
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
    private final LoginOrSignupWithGoogleCodeUsecase loginOrSignupWithGoogleCodeUsecase;
    private final CookieWriter cookieWriter;

    @PostMapping("/oauth2/google/callback")
    public ResponseEntity<ApiResponse<AuthResponse, Void>> googleCallback(
        @Valid @RequestBody GoogleAuthCodeRequest request,
        @RequestParam(value = "redirect_uri", required = false) String clientRedirectUri,
        HttpServletResponse servletResponse
    ) {
        GoogleLoginCommand command = AuthRequestMapper.toGoogleLoginCommand(
            request,
            clientRedirectUri
        );

        AuthResult authResult = loginOrSignupWithGoogleCodeUsecase.execute(command);

        authResult.refreshTokenCookie().ifPresent(instruction ->
            cookieWriter.write(servletResponse, instruction)
        );
        AuthResponse body = AuthResponseMapper.toResponse(authResult);

        return ResponseEntity.ok(ApiResponse.ok(LOGIN_SIGNUP_SUCCESS, body));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<NewAccessTokenResult, Void>> tokenRefresh(
        @CookieValue("refreshToken") String refreshToken,
        HttpServletResponse response
    ) {
        NewAccessTokenResult newAccessTokenResult = authService.createNewAccessToken(
            refreshToken
        );
        return ResponseEntity.ok(
            ApiResponse.ok(ACCESS_TOKEN_REFRESH_SUCCESS, newAccessTokenResult)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void, Void>> deleteRefreshToken() {
        authService.deleteRefreshToken();
        return ResponseEntity.ok(ApiResponse.ok(REFRESH_TOKEN_DELETED));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResult, Void>> registerUser(
        @RequestBody @Valid SignupRequest request,
        HttpServletResponse response
    ) {
        SignupCommand serviceRequest = AuthRequestMapper.toSignupServiceRequest(request);
        SignupResult signupResult = authService.registerUser(serviceRequest);
        return ResponseEntity.ok(ApiResponse.ok(SIGNUP_SUCCESS, signupResult));
    }
}

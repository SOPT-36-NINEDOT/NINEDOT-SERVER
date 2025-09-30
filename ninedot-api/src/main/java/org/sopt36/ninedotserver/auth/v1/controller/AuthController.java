package org.sopt36.ninedotserver.auth.v1.controller;

import static org.sopt36.ninedotserver.auth.exception.AuthErrorCode.UNAUTHORIZED;
import static org.sopt36.ninedotserver.auth.v1.message.AuthMessage.ACCESS_TOKEN_REFRESH_SUCCESS;
import static org.sopt36.ninedotserver.auth.v1.message.AuthMessage.LOGIN_SIGNUP_SUCCESS;
import static org.sopt36.ninedotserver.auth.v1.message.AuthMessage.REFRESH_TOKEN_DELETED;
import static org.sopt36.ninedotserver.auth.v1.message.AuthMessage.SIGNUP_SUCCESS;
import static org.sopt36.ninedotserver.global.web.CookieInstruction.clearRefreshToken;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt36.ninedotserver.auth.dto.command.GoogleLoginCommand;
import org.sopt36.ninedotserver.auth.dto.command.LogoutCommand;
import org.sopt36.ninedotserver.auth.dto.command.RefreshCommand;
import org.sopt36.ninedotserver.auth.dto.command.SignupCommand;
import org.sopt36.ninedotserver.auth.dto.result.SignupThenLoginResult;
import org.sopt36.ninedotserver.auth.dto.result.AuthResult;
import org.sopt36.ninedotserver.auth.dto.result.RefreshResult;
import org.sopt36.ninedotserver.auth.dto.security.PrincipalDto;
import org.sopt36.ninedotserver.auth.exception.AuthException;
import org.sopt36.ninedotserver.auth.port.in.LoginOrSignupWithGoogleCodeUsecase;
import org.sopt36.ninedotserver.auth.port.in.LogoutUsecase;
import org.sopt36.ninedotserver.auth.port.in.RefreshAccessTokenUsecase;
import org.sopt36.ninedotserver.auth.port.in.SignupUsecase;
import org.sopt36.ninedotserver.auth.v1.dto.request.GoogleAuthCodeRequest;
import org.sopt36.ninedotserver.auth.v1.dto.request.SignupRequest;
import org.sopt36.ninedotserver.auth.v1.dto.response.AuthResponse;
import org.sopt36.ninedotserver.auth.v1.dto.response.RefreshResponse;
import org.sopt36.ninedotserver.auth.v1.dto.response.SignupThenLoginResponse;
import org.sopt36.ninedotserver.auth.v1.mapper.AuthRequestMapper;
import org.sopt36.ninedotserver.auth.v1.mapper.AuthResponseMapper;
import org.sopt36.ninedotserver.dto.response.ApiResponse;
import org.sopt36.ninedotserver.global.web.CookieInstruction;
import org.sopt36.ninedotserver.global.web.CookieWriter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final LoginOrSignupWithGoogleCodeUsecase loginOrSignupWithGoogleCodeUsecase;
    private final RefreshAccessTokenUsecase refreshAccessTokenUsecase;
    private final LogoutUsecase logoutUsecase;
    private final SignupUsecase signupUsecase;
    private final CookieWriter cookieWriter;

    @PostMapping("/oauth2/google/callback")
    public ResponseEntity<ApiResponse<AuthResponse, Void>> googleCallback(
        @Valid @RequestBody GoogleAuthCodeRequest request,
        @RequestParam(value = "redirect_uri", required = false) String clientRedirectUri,
        HttpServletResponse servletResponse
    ) {
        log.info("[로그인 요청 시작]");

        GoogleLoginCommand command = AuthRequestMapper.toGoogleLoginCommand(
            request,
            clientRedirectUri
        );

        AuthResult authResult = loginOrSignupWithGoogleCodeUsecase.execute(command);

        authResult.refreshToken().ifPresent(refreshToken ->
            cookieWriter.write(servletResponse, CookieInstruction.setRefreshToken(refreshToken))
        );
        AuthResponse body = AuthResponseMapper.toOAuthResponse(authResult);

        log.info("[로그인 요청 완료]");

        return ResponseEntity.ok(ApiResponse.ok(LOGIN_SIGNUP_SUCCESS, body));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshResponse, Void>> tokenRefresh(
        @CookieValue(value = "refreshToken", required = false) String refreshToken,
        HttpServletResponse servletResponse
    ) {
        log.info("[토큰 재발급 요청 시작]");

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new AuthException(UNAUTHORIZED);
        }

        RefreshCommand refreshCommand = AuthRequestMapper.toRefreshCommand(refreshToken);
        RefreshResult refreshResult = refreshAccessTokenUsecase.execute(refreshCommand);

        cookieWriter.write(
            servletResponse,
            CookieInstruction.setRefreshToken(refreshResult.refreshToken())
        );

        RefreshResponse body = AuthResponseMapper.toRefreshResponse(refreshResult);

        log.info("[토큰 재발급 완료]");

        return ResponseEntity.ok(ApiResponse.ok(ACCESS_TOKEN_REFRESH_SUCCESS, body));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void, Void>> deleteRefreshToken(
        @AuthenticationPrincipal PrincipalDto principal,
        HttpServletResponse servletResponse
    ) {
        log.info("[로그아웃 요청 시작]");

        LogoutCommand logoutCommand = AuthRequestMapper.toLogoutCommand(principal.userId());
        logoutUsecase.execute(logoutCommand);
        cookieWriter.write(servletResponse, clearRefreshToken());

        log.info("[로그아웃 요청 완료]");

        return ResponseEntity.ok(ApiResponse.ok(REFRESH_TOKEN_DELETED));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupThenLoginResponse, Void>> registerUser(
        @RequestBody @Valid SignupRequest request,
        HttpServletResponse servletResponse
    ) {
        log.info("[회원가입 요청 시작]");
        SignupCommand serviceRequest = AuthRequestMapper.toSignupServiceRequest(request);
        SignupThenLoginResult signupResult = signupUsecase.execute(serviceRequest);
        SignupThenLoginResponse body = AuthResponseMapper.toSignupThenLoginResponse(signupResult);

        cookieWriter.write(
            servletResponse,
            CookieInstruction.setRefreshToken(signupResult.issuedTokens().refreshToken())
        );

        log.info("[회원가입 요청 완료]");

        return ResponseEntity.ok(ApiResponse.ok(SIGNUP_SUCCESS, body));
    }
}

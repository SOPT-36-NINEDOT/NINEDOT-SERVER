package org.sopt36.ninedotserver.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.auth.dto.request.GoogleAuthCodeRequest;
import org.sopt36.ninedotserver.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public ResponseEntity<?> googleCallback(@RequestBody GoogleAuthCodeRequest request,
        HttpServletResponse response) {
        return authService.loginOrSignupWithCode(request.code(), response);
    }
}

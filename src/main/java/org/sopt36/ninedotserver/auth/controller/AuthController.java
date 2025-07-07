package org.sopt36.ninedotserver.auth.controller;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.auth.service.AuthService;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;

}

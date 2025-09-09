package org.sopt36.ninedotserver.auth.service.login;

import org.sopt36.ninedotserver.auth.dto.command.GoogleLoginCommand;
import org.sopt36.ninedotserver.auth.port.in.LoginOrSignupWithGoogleCodeUsecase;
import org.sopt36.ninedotserver.auth.dto.result.AuthResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginOrSignupWithGoogleCodeService implements LoginOrSignupWithGoogleCodeUsecase {

    @Transactional
    @Override
    public AuthResult execute(GoogleLoginCommand googleLoginCommand) {
        return null;
    }
}

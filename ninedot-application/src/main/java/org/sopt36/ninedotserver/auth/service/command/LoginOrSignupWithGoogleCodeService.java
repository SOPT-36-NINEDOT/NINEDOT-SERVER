package org.sopt36.ninedotserver.auth.service.command;

import org.sopt36.ninedotserver.auth.port.in.command.GoogleLoginCommand;
import org.sopt36.ninedotserver.auth.port.in.command.LoginOrSignupWithGoogleCodeUsecase;
import org.sopt36.ninedotserver.auth.port.in.result.AuthResult;
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

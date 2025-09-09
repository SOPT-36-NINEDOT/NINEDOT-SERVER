package org.sopt36.ninedotserver.auth.port.in.command;

import org.sopt36.ninedotserver.auth.port.in.result.AuthResult;

public interface LoginOrSignupWithGoogleCodeUsecase {

    AuthResult execute(GoogleLoginCommand googleLoginCommand);
}

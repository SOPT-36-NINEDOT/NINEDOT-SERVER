package org.sopt36.ninedotserver.auth.port.in;

import org.sopt36.ninedotserver.auth.dto.command.GoogleLoginCommand;
import org.sopt36.ninedotserver.auth.dto.result.AuthResult;

public interface LoginOrSignupWithGoogleCodeUsecase {

    AuthResult execute(GoogleLoginCommand googleLoginCommand);
}

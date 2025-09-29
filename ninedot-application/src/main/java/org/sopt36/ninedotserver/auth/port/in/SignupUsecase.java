package org.sopt36.ninedotserver.auth.port.in;

import org.sopt36.ninedotserver.auth.dto.command.SignupCommand;
import org.sopt36.ninedotserver.auth.dto.response.SignupThenLoginResult;

public interface SignupUsecase {

    SignupThenLoginResult execute(SignupCommand signupCommand);
}

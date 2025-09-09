package org.sopt36.ninedotserver.auth.adapter.in.web.v1.mapper;

import org.sopt36.ninedotserver.auth.adapter.in.web.v1.dto.request.GoogleAuthCodeRequest;
import org.sopt36.ninedotserver.auth.adapter.in.web.v1.dto.request.SignupRequest;
import org.sopt36.ninedotserver.auth.dto.command.GoogleLoginCommand;
import org.sopt36.ninedotserver.auth.dto.command.SignupCommand;

public class AuthRequestMapper {

    public static SignupCommand toSignupServiceRequest(SignupRequest apiRequest) {
        return new SignupCommand(
            apiRequest.socialProvider(),
            apiRequest.socialToken(),
            apiRequest.name(),
            apiRequest.email(),
            apiRequest.birthday(),
            apiRequest.job(),
            apiRequest.profileImageUrl(),
            apiRequest.answers()
        );
    }

    public static GoogleLoginCommand toGoogleLoginCommand(
        GoogleAuthCodeRequest apiRequest,
        String clientRedirectUri
    ) {
        return new GoogleLoginCommand(
            apiRequest.code(),
            clientRedirectUri
        );
    }

}

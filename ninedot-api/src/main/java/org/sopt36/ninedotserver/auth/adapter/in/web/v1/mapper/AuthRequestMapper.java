package org.sopt36.ninedotserver.auth.adapter.in.web.v1.mapper;

import org.sopt36.ninedotserver.auth.dto.request.SignupCommand;
import org.sopt36.ninedotserver.auth.adapter.in.web.v1.dto.request.SignupRequest;

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

}

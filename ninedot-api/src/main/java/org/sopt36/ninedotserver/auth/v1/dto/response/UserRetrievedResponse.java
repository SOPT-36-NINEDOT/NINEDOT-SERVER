package org.sopt36.ninedotserver.auth.v1.dto.response;

import org.sopt36.ninedotserver.auth.dto.response.UserRetrievedResult;

public record UserRetrievedResponse(
    Long id,
    String email,
    String name,
    String job,
    String birthday
) {

    public static UserRetrievedResponse from(UserRetrievedResult userRetrievedResult) {
        return new UserRetrievedResponse(
            userRetrievedResult.id(),
            userRetrievedResult.email(),
            userRetrievedResult.name(),
            userRetrievedResult.job(),
            userRetrievedResult.birthday()
        );
    }
}

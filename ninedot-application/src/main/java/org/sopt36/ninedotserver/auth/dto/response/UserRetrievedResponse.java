package org.sopt36.ninedotserver.auth.dto.response;

import org.sopt36.ninedotserver.user.model.User;

public record UserRetrievedResponse(
    Long id,
    String email,
    String name,
    String job,
    String birthday
) {

    public static UserRetrievedResponse of(User user) {
        return new UserRetrievedResponse(
            user.getId(), user.getEmail(), user.getName(), user.getJob(), user.getBirthday()
        );
    }

}

package org.sopt36.ninedotserver.auth.dto.response;

import org.sopt36.ninedotserver.user.model.User;

public record UserRetrievedResult(
    Long id,
    String email,
    String name,
    String job,
    String birthday
) {

    public static UserRetrievedResult of(User user) {
        return new UserRetrievedResult(
            user.getId(), user.getEmail(), user.getName(), user.getJob(), user.getBirthday()
        );
    }

}

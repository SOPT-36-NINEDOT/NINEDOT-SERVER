package org.sopt36.ninedotserver.auth.dto.result;

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
            user.getId(), user.emailAsString(), user.nameAsString(), user.jobAsString(),
            user.birthdayAsString()
        );
    }

}

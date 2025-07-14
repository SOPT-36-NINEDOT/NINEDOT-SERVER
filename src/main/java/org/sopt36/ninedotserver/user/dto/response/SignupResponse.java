package org.sopt36.ninedotserver.user.dto.response;

import org.sopt36.ninedotserver.user.domain.User;

public record SignupResponse(
    Long id,
    String email,
    String name,
    String job,
    String birthday
) {

    public static SignupResponse of(User user) {
        return new SignupResponse(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getJob(),
            user.getBirthday()
        );
    }
}

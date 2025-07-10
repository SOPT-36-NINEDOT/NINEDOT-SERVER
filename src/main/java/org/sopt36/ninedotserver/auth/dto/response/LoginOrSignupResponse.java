package org.sopt36.ninedotserver.auth.dto.response;

public record LoginOrSignupResponse<T>(
    int code,
    T data,
    String message
) {

    public record LoginData(
        boolean exists,
        String accessToken
    ) {

    }

    public record SignupData(
        boolean exists,
        String name,
        String email
    ) {

    }
}

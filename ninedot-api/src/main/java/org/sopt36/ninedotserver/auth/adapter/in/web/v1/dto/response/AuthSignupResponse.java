package org.sopt36.ninedotserver.auth.adapter.in.web.v1.dto.response;

public record AuthSignupResponse(
    boolean exists,
    String socialProvider,
    String socialToken,
    String name,
    String email,
    String profileImageUrl
) implements AuthResponse {

}

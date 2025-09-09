package org.sopt36.ninedotserver.auth.adapter.in.web.v1.dto.response;

public record AuthSignupResponse(
    String socialProvider,
    String socialToken,
    boolean exists,
    String name,
    String email,
    String profileImageUrl
) implements AuthResponse {

}

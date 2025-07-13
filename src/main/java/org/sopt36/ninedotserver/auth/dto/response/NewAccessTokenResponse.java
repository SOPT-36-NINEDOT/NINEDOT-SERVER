package org.sopt36.ninedotserver.auth.dto.response;

public record NewAccessTokenResponse(
    String accessToken,
    String message
) {

}

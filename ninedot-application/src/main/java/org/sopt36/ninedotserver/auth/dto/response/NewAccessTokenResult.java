package org.sopt36.ninedotserver.auth.dto.response;

public record NewAccessTokenResult(
    String accessToken,
    String message
) {

}

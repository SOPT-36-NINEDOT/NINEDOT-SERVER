package org.sopt36.ninedotserver.auth.dto.result;

public record RefreshResult(
    String accessToken,
    String refreshToken
) {

}

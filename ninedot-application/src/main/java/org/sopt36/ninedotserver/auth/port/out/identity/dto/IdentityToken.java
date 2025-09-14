package org.sopt36.ninedotserver.auth.port.out.identity.dto;

public record IdentityToken(String accessToken, String refreshToken, Long expiresInSec) {

}

package org.sopt36.ninedotserver.auth.adapter.out.google.dto;

public record GoogleTokenResponse(String access_token, String refresh_token, Long expires_in) {

}

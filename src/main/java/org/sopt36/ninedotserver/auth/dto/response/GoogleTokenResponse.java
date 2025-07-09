package org.sopt36.ninedotserver.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleTokenResponse(@JsonProperty("access_token") String accessToken,
                                  @JsonProperty("expires_in") Integer expiresIn,
                                  String scope,
                                  @JsonProperty("token_type") String tokenType,
                                  @JsonProperty("id_token") String idToken) {

}

package org.sopt36.ninedotserver.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleUserInfo(
    String sub,
    String name,
    String email,
    @JsonProperty("picture") String profileImageUrl
) {

}

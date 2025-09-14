package org.sopt36.ninedotserver.auth.adapter.out.google.mapper;

import org.sopt36.ninedotserver.auth.adapter.out.google.dto.GoogleTokenResponse;
import org.sopt36.ninedotserver.auth.adapter.out.google.dto.GoogleUserInfoResponse;
import org.sopt36.ninedotserver.auth.port.out.identity.dto.IdentityToken;
import org.sopt36.ninedotserver.auth.port.out.identity.dto.IdentityUserInfo;

public class GoogleIdentityMapper {

    public static IdentityToken toPortToken(GoogleTokenResponse googleTokenResponse) {
        return new IdentityToken(
            googleTokenResponse.access_token(),
            googleTokenResponse.refresh_token(),
            googleTokenResponse.expires_in()
        );
    }

    public static IdentityUserInfo toPortUserInfo(GoogleUserInfoResponse googleUserInfoResponse) {
        return new IdentityUserInfo(
            googleUserInfoResponse.sub(),
            googleUserInfoResponse.email(),
            googleUserInfoResponse.name(),
            googleUserInfoResponse.picture()
        );
    }
}

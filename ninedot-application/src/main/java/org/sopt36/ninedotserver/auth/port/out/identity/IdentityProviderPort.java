package org.sopt36.ninedotserver.auth.port.out.identity;

import org.sopt36.ninedotserver.auth.port.out.identity.dto.IdentityToken;
import org.sopt36.ninedotserver.auth.port.out.identity.dto.IdentityUserInfo;

public interface IdentityProviderPort {

    IdentityToken exchangeAuthCode(String code, String redirectUri);

    IdentityUserInfo fetchUserInfo(String accessToken);
}

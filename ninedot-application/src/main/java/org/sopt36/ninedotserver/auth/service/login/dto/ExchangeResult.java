package org.sopt36.ninedotserver.auth.service.login.dto;

import org.sopt36.ninedotserver.auth.port.out.identity.dto.IdentityUserInfo;

public record ExchangeResult(
    String providerAccessToken,
    IdentityUserInfo identityUserInfo
) {

}

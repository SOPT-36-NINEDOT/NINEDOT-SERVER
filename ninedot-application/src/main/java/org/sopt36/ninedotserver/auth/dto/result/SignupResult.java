package org.sopt36.ninedotserver.auth.dto.result;

import java.util.Optional;
import org.sopt36.ninedotserver.auth.model.ProviderType;

public record SignupResult(
    ProviderType provider,
    String providerUserId,
    String name,
    String email,
    String picture,
    Optional<String> refreshToken
) implements AuthResult {

}

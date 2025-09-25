package org.sopt36.ninedotserver.auth.port.in;

import org.sopt36.ninedotserver.auth.dto.security.PrincipalDto;

public interface ResolvePrincipalByTokenUsecase {

    PrincipalDto execute(String token);
}

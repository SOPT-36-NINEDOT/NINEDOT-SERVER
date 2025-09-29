package org.sopt36.ninedotserver.auth.service.token;

import static org.sopt36.ninedotserver.auth.exception.AuthErrorCode.INVALID_TOKEN_FORMAT;
import static org.sopt36.ninedotserver.user.exception.UserErrorCode.USER_NOT_FOUND;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.auth.dto.security.PrincipalDto;
import org.sopt36.ninedotserver.auth.dto.security.TokenClaims;
import org.sopt36.ninedotserver.auth.exception.AuthException;
import org.sopt36.ninedotserver.auth.port.in.ResolvePrincipalByTokenUsecase;
import org.sopt36.ninedotserver.auth.port.out.token.TokenParsePort;
import org.sopt36.ninedotserver.auth.port.out.token.TokenVerifyPort;
import org.sopt36.ninedotserver.user.exception.UserException;
import org.sopt36.ninedotserver.user.port.out.UserQueryPort;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ResolvePrincipalByTokenHandler implements ResolvePrincipalByTokenUsecase {

    private final TokenVerifyPort tokenVerifyPort;
    private final TokenParsePort tokenParsePort;
    private final UserQueryPort userQueryPort;

    @Override
    public PrincipalDto execute(String token) {
        TokenClaims claimsResult = tokenParsePort.parseClaims(token);
        Long userId = claimsResult.userId();

        if (!userQueryPort.existsById(userId)) {
            throw new UserException(USER_NOT_FOUND);
        }

        return new PrincipalDto(userId);
    }
}

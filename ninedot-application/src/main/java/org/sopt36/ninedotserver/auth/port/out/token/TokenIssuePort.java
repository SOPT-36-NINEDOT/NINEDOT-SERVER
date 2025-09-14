package org.sopt36.ninedotserver.auth.port.out.token;

public interface TokenIssuePort {

    String issueAccessToken(Long userId, long expiresMs);

}

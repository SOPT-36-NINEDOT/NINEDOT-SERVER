package org.sopt36.ninedotserver.auth.port.out;

public interface TokenIssuePort {

    String issueAccessToken(Long userId, long expiresMs);

}

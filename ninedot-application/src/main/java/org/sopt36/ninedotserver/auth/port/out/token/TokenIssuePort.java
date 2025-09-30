package org.sopt36.ninedotserver.auth.port.out.token;

public interface TokenIssuePort {

    String createToken(Long id, long expirationMilliSeconds);

}

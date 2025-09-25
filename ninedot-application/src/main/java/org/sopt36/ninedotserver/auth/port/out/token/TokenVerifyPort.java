package org.sopt36.ninedotserver.auth.port.out.token;

public interface TokenVerifyPort {

    boolean validateToken(String token);

}

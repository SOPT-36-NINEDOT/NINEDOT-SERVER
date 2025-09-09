package org.sopt36.ninedotserver.auth.port.out;

public interface RedirectUriValidationPort {

    String resolveAndValidate(String clientRedirectUri);

}

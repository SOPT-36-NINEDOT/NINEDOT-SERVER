package org.sopt36.ninedotserver.auth.port.out.policy;

public interface RedirectUriValidationPort {

    String resolveAndValidate(String clientRedirectUri);

}

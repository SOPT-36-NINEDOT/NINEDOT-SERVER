package org.sopt36.ninedotserver.auth.adapter.out.redirect;

import java.util.List;
import org.sopt36.ninedotserver.auth.port.out.policy.RedirectUriValidationPort;
import org.springframework.stereotype.Component;

@Component
public class RedirectUriWhitelistValidator implements RedirectUriValidationPort {

    private final List<String> whitelist;
    private final String defaultRedirectUri;

    public RedirectUriWhitelistValidator(RedirectProperties redirectProperties) {
        this.whitelist = redirectProperties.getWhitelist();
        this.defaultRedirectUri = redirectProperties.getDefaultRedirectUri();
    }

    @Override
    public String resolveAndValidate(String clientProvidedRedirectUri) {
        if (clientProvidedRedirectUri != null && whitelist.contains(clientProvidedRedirectUri)) {
            return clientProvidedRedirectUri;
        }
        return defaultRedirectUri;
    }
}

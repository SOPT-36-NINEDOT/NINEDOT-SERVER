package org.sopt36.ninedotserver.auth.adapter.out.redirect;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "auth.redirect")
public class RedirectProperties {

    private List<String> whitelist;
    private String defaultRedirectUri;

}

package org.sopt36.ninedotserver.auth.adapter.out.google;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "google")
public class GoogleOAuthProperties {

    private String clientId;
    private String clientSecret;
    private String tokenUri;
    private String userinfoUri;
}

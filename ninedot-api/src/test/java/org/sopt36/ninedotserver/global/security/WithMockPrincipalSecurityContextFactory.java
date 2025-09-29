package org.sopt36.ninedotserver.global.security;

import org.sopt36.ninedotserver.auth.dto.security.PrincipalDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import java.util.Collections;

public class WithMockPrincipalSecurityContextFactory implements
    WithSecurityContextFactory<WithMockPrincipal> {

    @Override
    public SecurityContext createSecurityContext(WithMockPrincipal annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        PrincipalDto principal = new PrincipalDto(annotation.userId());
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null,
            Collections.emptyList());
        context.setAuthentication(auth);
        return context;
    }
}

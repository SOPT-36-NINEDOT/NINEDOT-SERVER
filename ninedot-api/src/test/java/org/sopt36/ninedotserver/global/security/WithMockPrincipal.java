package org.sopt36.ninedotserver.global.security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockPrincipalSecurityContextFactory.class)
public @interface WithMockPrincipal {

    long userId() default 1L;
}
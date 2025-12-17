package org.sopt36.ninedotserver.global.security;

import static org.sopt36.ninedotserver.auth.exception.AuthErrorCode.EXPIRED_ACCESS_TOKEN;
import static org.sopt36.ninedotserver.auth.exception.AuthErrorCode.EXPIRED_TOKEN;
import static org.sopt36.ninedotserver.auth.exception.AuthErrorCode.UNAUTHORIZED;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt36.ninedotserver.auth.dto.security.PrincipalDto;
import org.sopt36.ninedotserver.auth.exception.AuthErrorCode;
import org.sopt36.ninedotserver.auth.exception.AuthException;
import org.sopt36.ninedotserver.auth.port.in.ResolvePrincipalByTokenUsecase;
import org.sopt36.ninedotserver.exception.ErrorCode;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final ResolvePrincipalByTokenUsecase resolvePrincipalByTokenUsecase;
    private final JwtAuthenticationFactory jwtAuthenticationFactory;
    private final JsonAuthenticationEntryPoint jsonAuthenticationEntryPoint;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        List<String> skipPaths = List.of(
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api/*/jobs",
            "/api/*/persona",
            "/api/*/auth/signup",
            "/api/*/auth/refresh",
            "/api/*/auth/oauth2/google/callback"
        );
        String uri = request.getRequestURI();
        if (skipPaths.stream()
            .anyMatch(p -> new AntPathMatcher().match(p, uri))) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = resolveToken(request);

            if (token == null) {
                throw new AuthException(UNAUTHORIZED);
            }

            PrincipalDto principal = resolvePrincipalByTokenUsecase.execute(token);
            Authentication auth = jwtAuthenticationFactory.getAuthentication(principal);
            SecurityContextHolder.getContext().setAuthentication(auth);

            filterChain.doFilter(request, response);
        } catch (AuthException e) {
            SecurityContextHolder.clearContext();

            AuthException specificException = getAuthException(e);

            jsonAuthenticationEntryPoint.commence(
                request,
                response,
                new InsufficientAuthenticationException(
                    specificException.getMessage(),
                    specificException
                )
            );
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            String token = bearer.substring(7).trim();
            return token.isEmpty() ? null : token;
        }
        return null;
    }

    private AuthException getAuthException(AuthException e) {
        ErrorCode errorCode = e.getErrorCode();
        AuthException specificException;

        if (errorCode instanceof AuthErrorCode authErrorCode &&
            authErrorCode == EXPIRED_TOKEN) {
            specificException = new AuthException(EXPIRED_ACCESS_TOKEN);
        } else {
            specificException = e;
        }
        return specificException;
    }
}

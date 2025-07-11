package org.sopt36.ninedotserver.global.config.security;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        List<String> skipPaths = List.of(
            "/auth/jobs", "/auth/personas", "/auth/signup", "/auth/refresh",
            "/auth/oauth2/google/callback"
        );
        String uri = request.getRequestURI();
        if (skipPaths.contains(uri)) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = resolveToken(request);
        log.debug("추출된 토큰: {}", token);
        if (token != null && jwtProvider.validateToken(token)) {
            Authentication auth = jwtProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
            log.debug("인증 성공: {}", auth.getName());
        } else {
            log.debug("토큰이 없거나, 유효하지 않습니다.");
        }

        // 5) 필터 체인 계속 진행
        filterChain.doFilter(request, response);
    }


    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7).trim();
        }
        return null;
    }
}

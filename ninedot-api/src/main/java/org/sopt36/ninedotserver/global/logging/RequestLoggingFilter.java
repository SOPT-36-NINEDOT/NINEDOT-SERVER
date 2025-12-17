package org.sopt36.ninedotserver.global.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@Order(0)
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest req,
        @NonNull HttpServletResponse res,
        FilterChain chain
    ) throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("requestId", requestId);
        long start = System.currentTimeMillis();
        try {
            chain.doFilter(req, res);
        } finally {
            long took = System.currentTimeMillis() - start;
            String uri = req.getRequestURI();
            String query = req.getQueryString();
            String method = req.getMethod();
            String ip = req.getRemoteAddr();
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String principal = auth != null ? auth.getName() : "anonymous";

            log.info("[requestId: {}] {} {}{} -> status={} user={} ip={} took={}ms",
                requestId, method, uri, query != null ? "?" + query : "",
                res.getStatus(), principal, ip, took);
            MDC.clear();
        }
    }
}


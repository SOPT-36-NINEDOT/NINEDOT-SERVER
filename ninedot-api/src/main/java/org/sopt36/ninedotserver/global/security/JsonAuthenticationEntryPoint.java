package org.sopt36.ninedotserver.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.sopt36.ninedotserver.dto.response.ApiResponse;
import org.sopt36.ninedotserver.dto.response.ErrorMeta;
import org.sopt36.ninedotserver.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException
    ) throws IOException {
        Throwable cause = authException.getCause();
        if (cause instanceof BusinessException be) {
            ErrorMeta meta = new ErrorMeta(request.getRequestURI(), System.currentTimeMillis());
            ApiResponse<Void, ErrorMeta> apiResponse = ApiResponse.error(be.getErrorCode(), meta);

            response.setStatus(be.getErrorCode().getStatus().value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getOutputStream(), apiResponse);
            return;
        }

        ErrorMeta meta = new ErrorMeta(request.getRequestURI(), System.currentTimeMillis());
        ApiResponse<Void, ErrorMeta> apiResponse =
            ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "인증 실패", meta);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), apiResponse);
    }
}

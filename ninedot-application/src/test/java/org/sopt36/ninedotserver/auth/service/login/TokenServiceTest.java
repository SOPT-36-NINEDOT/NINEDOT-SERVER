package org.sopt36.ninedotserver.auth.service.login;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt36.ninedotserver.auth.port.out.RefreshTokenPort;
import org.sopt36.ninedotserver.auth.port.out.token.JwtProviderPort;
import org.sopt36.ninedotserver.auth.service.login.dto.IssuedTokens;
import org.sopt36.ninedotserver.auth.service.token.TokenService;
import org.sopt36.ninedotserver.auth.support.CookieInstruction;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    private final Long accessExpiration = 1_800_000L;
    private final Long refreshExpiration = 1_209_600_000L;

    @Mock
    JwtProviderPort jwtProviderPort;

    @Mock
    RefreshTokenPort refreshTokenPort;

    @InjectMocks
    TokenService tokenService;

    private static void setField(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    @BeforeEach
    void injectExpirations() throws Exception {
        setField(tokenService, "accessTokenExpirationMilliseconds", accessExpiration);
        setField(tokenService, "refreshTokenExpirationMilliseconds", refreshExpiration);
    }

    @Test
    @DisplayName("issueTokens: 액세스/리프레시 토큰 생성 후 저장/회전, 쿠키 지시문을 포함한 IssuedTokens 반환")
    void issueTokens_success() {
        // given
        Long userId = 42L;
        String accessToken = "AT.header.payload.sig";
        String refreshToken = "RT.header.payload.sig";

        when(jwtProviderPort.createToken(eq(userId), eq(accessExpiration))).thenReturn(accessToken);
        when(jwtProviderPort.createToken(eq(userId), eq(refreshExpiration)))
            .thenReturn(refreshToken);

        // when
        Instant before = Instant.now();
        IssuedTokens issued = tokenService.issueTokens(userId);
        Instant after = Instant.now();

        // then
        assertThat(issued).isNotNull();
        assertThat(issued.accessToken()).isEqualTo(accessToken);
        assertThat(issued.refreshTokenCookie()).isNotNull();

        verify(jwtProviderPort, times(1)).createToken(userId, accessExpiration);
        verify(jwtProviderPort, times(1)).createToken(userId, refreshExpiration);

        ArgumentCaptor<Instant> expCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(refreshTokenPort, times(1))
            .saveOrRotate(eq(userId), eq(refreshToken), expCaptor.capture());

        Instant captured = expCaptor.getValue();
        Instant expectedMin = before.plusMillis(refreshExpiration).minusSeconds(2);
        Instant expectedMax = after.plusMillis(refreshExpiration).plusSeconds(2);
        assertThat(captured).isAfterOrEqualTo(expectedMin).isBeforeOrEqualTo(expectedMax);
    }

    @Test
    @DisplayName("clearRefreshTokenInstruction: 리프레시 쿠키 제거 지시문을 반환한다")
    void clearRefreshTokenInstruction_returnsClearCookie() {
        // when
        CookieInstruction cookieInstruction = tokenService.clearRefreshTokenInstruction();

        // then
        assertThat(cookieInstruction).isNotNull();
    }
}
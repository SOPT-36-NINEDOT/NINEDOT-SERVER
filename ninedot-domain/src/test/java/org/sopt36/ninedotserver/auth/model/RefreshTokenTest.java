package org.sopt36.ninedotserver.auth.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sopt36.ninedotserver.user.model.User;

class RefreshTokenTest {

    @Test
    @DisplayName("create(user, token, expiresAt): 필드를 정확히 세팅한다")
    void create_setsAllFields() {
        // given
        User user = Mockito.mock(User.class);
        String refreshToken = "refreshtoken.header.payload.signature";
        Instant expiresAt = Instant.now().plusMillis(1209600000);

        // when
        RefreshToken refreshTokenDomain = RefreshToken.create(user, refreshToken, expiresAt);

        // then
        assertThat(refreshTokenDomain.getId()).isNull();
        assertThat(refreshTokenDomain.getUser()).isSameAs(user);
        assertThat(refreshTokenDomain.getRefreshToken()).isEqualTo(refreshToken);
        assertThat(refreshTokenDomain.getExpiresAt()).isEqualTo(expiresAt);
    }

    @Test
    @DisplayName("rotate(refreshToken, expiresAt): 새로운 RefreshToken으로 회전한다.")
    void rotate_refresh_token() {
        // given
        User user = Mockito.mock(User.class);
        String refreshToken = "refreshtoken.header.payload.signature";
        Instant expiresAt = Instant.now().plusMillis(1209600000);

        RefreshToken refreshTokenDomain = RefreshToken.create(user, refreshToken, expiresAt);

        String newRefreshToken = "newrefreshtoken.header.payload.signature";
        Instant newExpiresAt = Instant.now().plusMillis(1209600001);

        // when
        refreshTokenDomain.rotate(newRefreshToken, newExpiresAt);

        // then
        assertThat(refreshTokenDomain.getUser()).isSameAs(user);
        assertThat(refreshTokenDomain.getRefreshToken()).isEqualTo(newRefreshToken);
        assertThat(refreshTokenDomain.getExpiresAt()).isEqualTo(newExpiresAt);
    }

    @Test
    @DisplayName("getUserId(): User의 Id를 반환한다.")
    void getUserId_returnsUserId() {
        // given
        User mockUser = Mockito.mock(User.class);
        when(mockUser.getId()).thenReturn(1L);

        RefreshToken refreshTokenDomain = RefreshToken.create(
            mockUser,
            "refreshtoken.string",
            Instant.now()
        );

        // when
        Long actualUserId = refreshTokenDomain.getUserId();

        // then
        assertThat(actualUserId).isEqualTo(1L);
    }


}
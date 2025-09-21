package org.sopt36.ninedotserver.auth.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
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
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);

        // when
        RefreshToken refreshTokenDomain = RefreshToken.create(user, refreshToken, expiresAt);

        // then
        assertThat(refreshTokenDomain.getId()).isNull();
        assertThat(refreshTokenDomain.getUser()).isSameAs(user);
        assertThat(refreshTokenDomain.getRefreshToken()).isEqualTo(refreshToken);
        assertThat(refreshTokenDomain.getExpiresAt()).isEqualTo(expiresAt);
    }

}
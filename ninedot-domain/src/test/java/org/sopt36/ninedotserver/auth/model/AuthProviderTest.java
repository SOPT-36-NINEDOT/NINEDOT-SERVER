package org.sopt36.ninedotserver.auth.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sopt36.ninedotserver.user.model.User;

class AuthProviderTest {

    @Test
    @DisplayName("create(user, provider, providerUserId): 필드를 정확히 세팅한다")
    void create_setsAllFields() {
        // given
        User user = Mockito.mock(User.class);
        ProviderType provider = ProviderType.GOOGLE;
        String providerUserId = "1234567890";

        // when
        AuthProvider authProvider = AuthProvider.create(user, provider, providerUserId);

        // then
        assertThat(authProvider.getId()).isNull();
        assertThat(authProvider.getUser()).isSameAs(user);
        assertThat(authProvider.getProvider()).isEqualTo(provider);
        assertThat(authProvider.getProviderUserId()).isEqualTo(providerUserId);
    }
}

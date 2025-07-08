package org.sopt36.ninedotserver.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt36.ninedotserver.global.entity.BaseEntity;
import org.sopt36.ninedotserver.user.domain.User;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(access = AccessLevel.PROTECTED)
@Table(name = "auth_provider")
@Entity
public class AuthProvider extends BaseEntity {

    private static final int MAX_PROVIDER_LENGTH = 10;
    private static final int MAX_PROVIDER_USER_ID_LENGTH = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", length = MAX_PROVIDER_LENGTH, nullable = false)
    private ProviderType provider;

    @Column(name = "provider_user_id", length = MAX_PROVIDER_USER_ID_LENGTH, nullable = false, unique = true)
    private String providerUserId;

    public static AuthProvider create(User user, ProviderType provider, String providerUserId) {
        return AuthProvider.builder().user(user).provider(provider)
                   .providerUserId(providerUserId).build();
    }
}

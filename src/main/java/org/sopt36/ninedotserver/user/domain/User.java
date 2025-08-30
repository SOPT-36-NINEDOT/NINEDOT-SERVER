package org.sopt36.ninedotserver.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.sopt36.ninedotserver.global.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(access = AccessLevel.PROTECTED)
@Table(name = "user")
@DynamicInsert
@Entity
public class User extends BaseEntity {

    private static final int MAX_NAME_LENGTH = 10;
    private static final int MAX_BIRTHDAY_LENGTH = 20;
    private static final int MAX_JOB_LENGTH = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = MAX_NAME_LENGTH, nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Lob
    @Column(name = "profile_image_url", nullable = false)
    private String profileImageUrl;

    @Column(name = "birthday", length = MAX_BIRTHDAY_LENGTH, nullable = false)
    private String birthday;

    @Column(name = "job", length = MAX_JOB_LENGTH)
    private String job;

    @Column(name = "onboarding_completed", nullable = false)
    @ColumnDefault(value = "false")
    private Boolean onboardingCompleted;

    public static User create(
        String name,
        String email,
        String profileImageUrl,
        String birthday,
        String job
    ) {
        return User.builder()
            .name(name)
            .email(email)
            .profileImageUrl(profileImageUrl)
            .birthday(birthday)
            .job(job)
            .build();
    }

    public boolean isSameId(Long id) {
        if (id == null) {
            return false;
        }
        return this.id.equals(id);
    }

    public void completeOnboarding() {
        if (Boolean.FALSE.equals(this.onboardingCompleted)) {
            this.onboardingCompleted = true;
        }
    }
}

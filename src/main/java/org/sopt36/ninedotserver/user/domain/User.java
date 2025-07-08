package org.sopt36.ninedotserver.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    @Column(name = "profile_image_url", columnDefinition = "TEXT", nullable = false)
    private String profileImageUrl;

    @Column(name = "birthday", length = MAX_BIRTHDAY_LENGTH, nullable = false)
    private String birthday;

    @Enumerated(EnumType.STRING)
    @Column(name = "job", length = MAX_JOB_LENGTH)
    private JobType job;

    @Column(name = "onboarding_completed", nullable = false)
    @ColumnDefault(value = "false")
    private Boolean onboardingCompleted;

    public static User create(String name, String email, String profileImageUrl, String birthday,
        JobType job) {
        return User.builder()
                   .name(name)
                   .email(email)
                   .profileImageUrl(profileImageUrl)
                   .birthday(birthday)
                   .job(job)
                   .build();
    }
}

package org.sopt36.ninedotserver.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.sopt36.ninedotserver.global.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(access = AccessLevel.PROTECTED)
@Table(name = "user")
@Entity
@DynamicInsert
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 10, nullable = false)
    private String name;

    @Column(name = "email", length = 255, nullable = false, unique = true)
    private String email;

    @Column(name = "profile_image_url", nullable = false, unique = true)
    private String profileImageUrl;

    @Column(name = "birthday", length = 20, nullable = false)
    private String birthday;

    @Column(name = "job", length = 100)
    @Enumerated(EnumType.STRING)
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
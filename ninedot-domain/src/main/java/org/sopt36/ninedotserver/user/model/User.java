package org.sopt36.ninedotserver.user.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.sopt36.ninedotserver.entity.BaseEntity;
import org.sopt36.ninedotserver.user.model.value.Birthday;
import org.sopt36.ninedotserver.user.model.value.Email;
import org.sopt36.ninedotserver.user.model.value.ProfileImageUrl;
import org.sopt36.ninedotserver.user.model.value.UserName;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(access = AccessLevel.PROTECTED)
@Table(name = "user")
@DynamicInsert
@Entity
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "value",
            column = @Column(name = "name", length = UserName.MAX_LENGTH, nullable = false))
    private UserName name;

    @Embedded
    @AttributeOverride(name = "value",
            column = @Column(name = "email", nullable = false, unique = true))
    private Email email;

    @Embedded
    @AttributeOverride(name = "value",
            column = @Column(name = "profile_Image_Url", length = ProfileImageUrl.MAX_LENGTH, nullable = false))
    private ProfileImageUrl profileImageUrl;

    @Embedded
    @AttributeOverride(name = "value",
            column = @Column(name = "birthday", length = Birthday.MAX_LENGTH, nullable = false))
    private Birthday birthday;

    @Enumerated(EnumType.STRING)
    @Column(name = "job", length = JobType.MAX_LENGTH, nullable = false)
    private JobType job;

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
                .name(new UserName(name))
                .email(new Email(email))
                .profileImageUrl(new ProfileImageUrl(profileImageUrl))
                .birthday(new Birthday(birthday))
                .job(JobType.from(job))
                .onboardingCompleted(false)
                .build();
    }

    public String nameAsString() {
        return name.toString();
    }
    public String emailAsString() {
        return email.toString();
    }
    public String profileImageUrlAsString() {
        return profileImageUrl.toString();
    }
    public String birthdayAsString() {
        return birthday.toString();
    }
    public String jobAsString() {
        return job.toString();
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

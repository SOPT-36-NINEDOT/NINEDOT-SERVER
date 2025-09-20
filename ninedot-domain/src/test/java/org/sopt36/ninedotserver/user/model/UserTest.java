package org.sopt36.ninedotserver.user.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {

    @Test
    void 유저가_정상적으로_생성된다() {
        // given
        String name = "홍길동";
        String email = "test@example.com";
        String profileImageUrl = "http://example.com/profile.png";
        String birthday = "2000.01.01";
        String job = "DEVELOPER";

        // when
        User user = User.create(name, email, profileImageUrl, birthday, job);

        // then
        assertThat(user.nameAsString()).isEqualTo(name);
        assertThat(user.emailAsString()).isEqualTo(email);
        assertThat(user.profileImageUrlAsString()).isEqualTo(profileImageUrl);
        assertThat(user.birthdayAsString()).isEqualTo(birthday);
        assertThat(user.jobAsString()).isEqualTo(job);
        assertThat(user.getOnboardingCompleted()).isFalse();
    }

    @Test
    void 온보딩을_완료하면_onboardingCompleted가_true가_된다() {
        // given
        User user = User.create(
                "홍길동",
                "test@example.com",
                "http://img.com/1",
                "2000.01.01",
                "DEVELOPER");

        // when
        user.completeOnboarding();

        // then
        assertThat(user.getOnboardingCompleted()).isTrue();

    }

    @Test
    void 같은_id면_isSameId는_true다() {
        // given
        User user = User.create(
                "홍길동",
                "test@example.com",
                "http://img.com/1",
                "2000.01.01",
                "DEVELOPER");

        Long expectedId = 1L;
        user = User.builder()
                .id(expectedId)
                .name(user.getName())
                .email(user.getEmail())
                .profileImageUrl(user.getProfileImageUrl())
                .birthday(user.getBirthday())
                .job(user.getJob())
                .onboardingCompleted(user.getOnboardingCompleted())
                .build();

        // when & then
        assertThat(user.isSameId(expectedId)).isTrue();
        assertThat(user.isSameId(2L)).isFalse();
        assertThat(user.isSameId(null)).isFalse();
    }
}

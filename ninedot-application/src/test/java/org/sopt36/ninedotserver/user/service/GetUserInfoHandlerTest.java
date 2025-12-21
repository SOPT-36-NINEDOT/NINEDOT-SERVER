package org.sopt36.ninedotserver.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt36.ninedotserver.auth.model.OnboardingPage;
import org.sopt36.ninedotserver.auth.service.login.OnboardingStatusService;
import org.sopt36.ninedotserver.auth.service.login.dto.OnboardingStatus;
import org.sopt36.ninedotserver.user.dto.query.UserInfoQuery;
import org.sopt36.ninedotserver.user.dto.result.UserInfoResult;
import org.sopt36.ninedotserver.user.exception.UserErrorCode;
import org.sopt36.ninedotserver.user.exception.UserException;
import org.sopt36.ninedotserver.user.model.User;
import org.sopt36.ninedotserver.user.port.out.UserQueryPort;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserInfoHandlerTest {

    @Mock
    private UserQueryPort userQueryPort;

    @Mock
    private OnboardingStatusService onboardingStatusService;

    @InjectMocks
    private GetUserInfoHandler getUserInfoHandler;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.create(
                "테스트유저",
                "test@example.com",
                "https://profile.com/test.png",
                "2000.01.01",
                "DEVELOPER"
        );
    }

    @Test
    void 유저_정보와_온보딩_상태를_함께_조회한다() {
        // given
        Long userId = 1L;
        UserInfoQuery query = new UserInfoQuery(userId);

        OnboardingStatus onboardingStatus =
                new OnboardingStatus(false, OnboardingPage.CORE_GOAL);

        when(userQueryPort.findById(userId)).thenReturn(Optional.of(user));
        when(onboardingStatusService.determineOnboardingStatus(isNull()))
                .thenReturn(onboardingStatus);

        // when
        UserInfoResult result = getUserInfoHandler.execute(query);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(user.getId());
        assertThat(result.name()).isEqualTo(user.nameAsString());
        assertThat(result.email()).isEqualTo(user.emailAsString());
        assertThat(result.profileImageUrl()).isEqualTo(user.profileImageUrlAsString());
        assertThat(result.onboardingCompleted()).isFalse();
        assertThat(result.nextPage()).isEqualTo(OnboardingPage.CORE_GOAL);
    }

    @Test
    void 존재하지_않는_유저면_예외를_던진다() {
        // given
        Long userId = 99L;
        UserInfoQuery query = new UserInfoQuery(userId);

        when(userQueryPort.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> getUserInfoHandler.execute(query))
                .isInstanceOf(UserException.class)
                .extracting("errorCode")
                .isEqualTo(UserErrorCode.USER_NOT_FOUND);
    }
}
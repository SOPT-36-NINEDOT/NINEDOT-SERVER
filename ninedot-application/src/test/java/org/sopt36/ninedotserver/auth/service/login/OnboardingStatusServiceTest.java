package org.sopt36.ninedotserver.auth.service.login;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt36.ninedotserver.auth.model.OnboardingPage;
import org.sopt36.ninedotserver.auth.service.login.dto.OnboardingStatus;
import org.sopt36.ninedotserver.mandalart.port.out.CoreGoalRepositoryPort;
import org.sopt36.ninedotserver.mandalart.port.out.MandalartRepositoryPort;
import org.sopt36.ninedotserver.user.exception.UserErrorCode;
import org.sopt36.ninedotserver.user.exception.UserException;
import org.sopt36.ninedotserver.user.model.User;
import org.sopt36.ninedotserver.user.port.out.UserQueryPort;

@ExtendWith(MockitoExtension.class)
class OnboardingStatusServiceTest {

    @Mock
    UserQueryPort userQueryPort;

    @Mock
    CoreGoalRepositoryPort coreGoalRepositoryPort;

    @Mock
    MandalartRepositoryPort mandalartRepositoryPort;

    @InjectMocks
    OnboardingStatusService onboardingStatusService;

    @Nested
    @DisplayName("determineOnboardingStatus(userId)")
    class Determine {

        @Test
        @DisplayName("유저가 없으면 USER_NOT_FOUND 예외를 던진다")
        void userNotFound_throws() {
            // given
            Long userId = 1L;
            when(userQueryPort.findById(userId)).thenReturn(Optional.empty());

            // when / then
            UserException ex = assertThrows(UserException.class,
                () -> onboardingStatusService.determineOnboardingStatus(userId));
            assertThat(ex.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);

            verify(userQueryPort).findById(userId);
            verifyNoInteractions(coreGoalRepositoryPort, mandalartRepositoryPort);
        }

        @Test
        @DisplayName("온보딩 완료된 유저면 ONBOARDING_COMPLETED를 반환하고 추가 조회를 하지 않는다")
        void onboardingCompleted_returnsCompleted() {
            // given
            Long userId = 2L;
            User user = mock(User.class);
            when(user.getOnboardingCompleted()).thenReturn(Boolean.TRUE);
            when(userQueryPort.findById(userId)).thenReturn(Optional.of(user));

            // when
            OnboardingStatus status = onboardingStatusService.determineOnboardingStatus(userId);

            // then
            assertThat(status.onboardingCompleted()).isTrue();
            assertThat(status.nextPage()).isEqualTo(OnboardingPage.ONBOARDING_COMPLETED);

            verify(userQueryPort).findById(userId);
            verifyNoInteractions(coreGoalRepositoryPort, mandalartRepositoryPort);
        }

        @Test
        @DisplayName("미완료 + CoreGoal이 있으면 SUB_GOAL을 반환하고 Mandalart는 조회하지 않는다")
        void notCompleted_hasCoreGoal_returnsSubGoal() {
            // given
            Long userId = 3L;
            User user = mock(User.class);
            when(user.getOnboardingCompleted()).thenReturn(Boolean.FALSE);
            when(userQueryPort.findById(userId)).thenReturn(Optional.of(user));
            when(coreGoalRepositoryPort.existsByUserId(userId)).thenReturn(true);

            // when
            OnboardingStatus status = onboardingStatusService.determineOnboardingStatus(userId);

            // then
            assertThat(status.onboardingCompleted()).isFalse();
            assertThat(status.nextPage()).isEqualTo(OnboardingPage.SUB_GOAL);

            verify(userQueryPort).findById(userId);
            verify(coreGoalRepositoryPort).existsByUserId(userId);
            verifyNoInteractions(mandalartRepositoryPort);

            InOrder inOrder = inOrder(userQueryPort, coreGoalRepositoryPort);
            inOrder.verify(userQueryPort).findById(eq(userId));
            inOrder.verify(coreGoalRepositoryPort).existsByUserId(eq(userId));
        }

        @Test
        @DisplayName("미완료 + CoreGoal 없음 + Mandalart 있으면 CORE_GOAL을 반환한다")
        void notCompleted_hasMandalart_returnsCoreGoal() {
            // given
            Long userId = 4L;
            User user = mock(User.class);
            when(user.getOnboardingCompleted()).thenReturn(Boolean.FALSE);
            when(userQueryPort.findById(userId)).thenReturn(Optional.of(user));
            when(coreGoalRepositoryPort.existsByUserId(userId)).thenReturn(false);
            when(mandalartRepositoryPort.existsByUserId(userId)).thenReturn(true);

            // when
            OnboardingStatus status = onboardingStatusService.determineOnboardingStatus(userId);

            // then
            assertThat(status.onboardingCompleted()).isFalse();
            assertThat(status.nextPage()).isEqualTo(OnboardingPage.CORE_GOAL);

            InOrder inOrder = inOrder(userQueryPort, coreGoalRepositoryPort,
                mandalartRepositoryPort);
            inOrder.verify(userQueryPort).findById(eq(userId));
            inOrder.verify(coreGoalRepositoryPort).existsByUserId(eq(userId));
            inOrder.verify(mandalartRepositoryPort).existsByUserId(eq(userId));
            verifyNoMoreInteractions(userQueryPort, coreGoalRepositoryPort,
                mandalartRepositoryPort);
        }

        @Test
        @DisplayName("미완료 + CoreGoal 없음 + Mandalart 없음이면 MANDALART를 반환한다")
        void notCompleted_none_returnsMandalart() {
            // given
            Long userId = 5L;
            User user = mock(User.class);
            when(user.getOnboardingCompleted()).thenReturn(Boolean.FALSE);
            when(userQueryPort.findById(userId)).thenReturn(Optional.of(user));
            when(coreGoalRepositoryPort.existsByUserId(userId)).thenReturn(false);
            when(mandalartRepositoryPort.existsByUserId(userId)).thenReturn(false);

            // when
            OnboardingStatus status = onboardingStatusService.determineOnboardingStatus(userId);

            // then
            assertThat(status.onboardingCompleted()).isFalse();
            assertThat(status.nextPage()).isEqualTo(OnboardingPage.MANDALART);

            InOrder inOrder = inOrder(userQueryPort, coreGoalRepositoryPort,
                mandalartRepositoryPort);
            inOrder.verify(userQueryPort).findById(eq(userId));
            inOrder.verify(coreGoalRepositoryPort).existsByUserId(eq(userId));
            inOrder.verify(mandalartRepositoryPort).existsByUserId(eq(userId));
            verifyNoMoreInteractions(userQueryPort, coreGoalRepositoryPort,
                mandalartRepositoryPort);
        }
    }
}
package org.sopt36.ninedotserver.user.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt36.ninedotserver.user.dto.query.UserInfoQuery;
import org.sopt36.ninedotserver.user.dto.result.UserInfoResult;
import org.sopt36.ninedotserver.user.exception.UserErrorCode;
import org.sopt36.ninedotserver.user.exception.UserException;
import org.sopt36.ninedotserver.user.model.User;
import org.sopt36.ninedotserver.user.port.out.UserQueryPort;
import org.sopt36.ninedotserver.user.usecase.handler.GetUserInfoHandler;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserQueryServiceTest {

    @Mock
    private UserQueryPort userQueryPort;

    @InjectMocks
    private GetUserInfoHandler getUserInfoHandler;

    private User user;

    @BeforeEach
    void setup() {
        user = User.create(
                "테스트유저",
                "test@example.com",
                "https://profile.com/test.png",
                "2000.01.01",
                "DEVELOPER"
        );
    }

    @Test
    void 유저정보를_성공적으로_조회한다() {
        // given
        Long userId = 1L;
        UserInfoQuery query = new UserInfoQuery(userId);

        when(userQueryPort.findById(userId)).thenReturn(Optional.of(user));

        // when
        UserInfoResult result = getUserInfoHandler.getUserInfo(query);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(user.getId());
        assertThat(result.name()).isEqualTo(user.nameAsString());
        assertThat(result.email()).isEqualTo(user.emailAsString());
    }

    @Test
    void 존재하지_않는_유지이면_예외가_발생한다() {
        // given
        Long userId = 99L;
        UserInfoQuery query = new UserInfoQuery(userId);

        when(userQueryPort.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> getUserInfoHandler.getUserInfo(query))
                .isInstanceOf(UserException.class)
                .extracting("errorCode")
                .isEqualTo(UserErrorCode.USER_NOT_FOUND);
    }






}

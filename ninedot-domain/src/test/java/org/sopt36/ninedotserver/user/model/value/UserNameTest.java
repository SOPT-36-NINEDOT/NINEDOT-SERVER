package org.sopt36.ninedotserver.user.model.value;

import org.junit.jupiter.api.Test;
import org.sopt36.ninedotserver.user.exception.UserErrorCode;
import org.sopt36.ninedotserver.user.exception.UserException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserNameTest {

    @Test
    void 유효한_이름이면_생성된다() {
        // given
        String validName = "validName";

        // when
        UserName userName = new UserName(validName);

        //then
        assertThat(userName.value()).isEqualTo(validName);
    }

    @Test
    void 빈_이름이면_예외발생() {
        // given
        String blankName = " ";

        // when & then
        assertThatThrownBy(() -> new UserName(blankName))
                .isInstanceOf(UserException.class)
                .extracting(e -> ((UserException) e).getErrorCode())
                .isEqualTo(UserErrorCode.NAME_NOT_BLANK);
    }

    @Test
    void 너무_긴_이름이면_예외발생() {
        // given
        String tooLongName = "a".repeat(20);

        // when & then
        assertThatThrownBy(() -> new UserName(tooLongName))
                .isInstanceOf(UserException.class)
                .extracting(e -> ((UserException) e).getErrorCode())
                .isEqualTo(UserErrorCode.INVALID_NAME_LENGTH);
    }

}

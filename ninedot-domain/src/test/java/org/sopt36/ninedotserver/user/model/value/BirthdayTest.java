package org.sopt36.ninedotserver.user.model.value;

import org.junit.jupiter.api.Test;
import org.sopt36.ninedotserver.user.exception.UserErrorCode;
import org.sopt36.ninedotserver.user.exception.UserException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BirthdayTest {

    @Test
    void 유효한_생일이면_생성된다() {
        // given
        String validBirthday = "2000.01.01";

        // when
        Birthday birthday = new Birthday(validBirthday);

        // then
        assertThat(birthday.value()).isEqualTo(validBirthday);
    }

    @Test
    void 빈_생일이면_예외발생() {
        // given
        String blankBirthday = " ";

        // when & then
        assertThatThrownBy((() -> new Birthday(blankBirthday)))
                .isInstanceOf(UserException.class)
                .extracting(e -> ((UserException) e).getErrorCode())
                .isEqualTo(UserErrorCode.BIRTHDAY_NOT_BLANK);
    }

    @Test
    void 잘못된_형식의_생일이면_예외발생() {
        // given
        String invalidBirthday = "2025-01-01";

        // when & then
        assertThatThrownBy(() -> new Birthday(invalidBirthday))
                .isInstanceOf(UserException.class)
                .extracting(e -> ((UserException) e).getErrorCode())
                .isEqualTo(UserErrorCode.INVALID_BIRTHDAY_TYPE);
    }

    @Test
    void 너무_긴_생일이면_예외발생() {
        // given
        String tooLongBirthday = "2025123123121242.01.01";

        // when & then
        assertThatThrownBy(() -> new Birthday(tooLongBirthday))
                .isInstanceOf(UserException.class)
                .extracting(e -> ((UserException) e).getErrorCode())
                .isEqualTo(UserErrorCode.INVALID_BIRTHDAY_LENGTH);

    }
}

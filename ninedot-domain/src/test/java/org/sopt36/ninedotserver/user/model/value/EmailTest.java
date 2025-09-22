package org.sopt36.ninedotserver.user.model.value;

import org.junit.jupiter.api.Test;
import org.sopt36.ninedotserver.user.exception.UserErrorCode;
import org.sopt36.ninedotserver.user.exception.UserException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class EmailTest {

    @Test
    void 유효한_이메일이면_생성된다() {
        // given
        String validEmail = "test@example.coom";

        // when
        Email email = new Email(validEmail);

        // then
        assertThat(email.value()).isEqualTo(validEmail);
    }

    @Test
    void 빈_이메일이면_예외발생() {
        // given
        String invalidEmail = null;

        // when & then
        assertThatThrownBy(() -> new Email(invalidEmail))
                .isInstanceOf(UserException.class)
                .extracting(e -> ((UserException) e).getErrorCode())
                .isEqualTo(UserErrorCode.EMAIL_NOT_BLANK);

    }

    @Test
    void 너무_긴_이메일이면_예외발생() {
        // given
        String invalidEmail = "a".repeat(300);

        // when & then
        assertThatThrownBy(() -> new Email(invalidEmail))
                .isInstanceOf(UserException.class)
                .extracting(e -> ((UserException) e).getErrorCode())
                .isEqualTo(UserErrorCode.INVALID_EMAIL_LENGTH);
    }
}

package org.sopt36.ninedotserver.user.model.value;

import org.junit.jupiter.api.Test;
import org.sopt36.ninedotserver.user.exception.UserErrorCode;
import org.sopt36.ninedotserver.user.exception.UserException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProfileImageUrlTest {

    @Test
    void 유효한_URL이면_생성된다() {
        // given
        String validUrl = "https://example.com";

        // when
        ProfileImageUrl url = new ProfileImageUrl(validUrl);

        // then
        assertThat(url.value()).isEqualTo(validUrl);
    }

    @Test
    void 빈_URL이면_예외발생() {
        // given
        String blankUrl = " ";

        // when & then
        assertThatThrownBy(() -> new ProfileImageUrl(blankUrl))
            .isInstanceOf(UserException.class)
            .extracting(e -> ((UserException) e).getErrorCode())
            .isEqualTo(UserErrorCode.PROFILE_IMAGE_NOT_BLANK);
    }

    @Test
    void 너무_긴_URL이면_예외발생() {
        // given
        String tooLongUrl = "a".repeat(4097);

        // when & then
        assertThatThrownBy(() -> new ProfileImageUrl(tooLongUrl))
            .isInstanceOf(UserException.class)
            .extracting(e -> ((UserException) e).getErrorCode())
            .isEqualTo(UserErrorCode.PROFILE_IMAGE_URL_TOO_LONG);
    }

}

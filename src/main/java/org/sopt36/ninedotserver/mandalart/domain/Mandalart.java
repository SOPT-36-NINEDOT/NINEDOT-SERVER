package org.sopt36.ninedotserver.mandalart.domain;

import static org.sopt36.ninedotserver.mandalart.exception.MandalartErrorCode.INVALID_TITLE_LENGTH;
import static org.sopt36.ninedotserver.mandalart.exception.MandalartErrorCode.TITLE_NOT_BLANK;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt36.ninedotserver.global.entity.BaseEntity;
import org.sopt36.ninedotserver.mandalart.exception.MandalartException;
import org.sopt36.ninedotserver.user.domain.User;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Mandalart extends BaseEntity {

    private static final int MAX_TITLE_LENGTH = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 30, nullable = false)
    private String title;

    @Column(nullable = false)
    private boolean aiGeneratable;

    private Mandalart(User user, String title, boolean aiGeneratable) {
        validateTitle(title);
        this.user = user;
        this.title = title;
        this.aiGeneratable = aiGeneratable;
    }

    public static Mandalart create(User user, String title, boolean aiGeneratable) {
        return new Mandalart(user, title, aiGeneratable);
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new MandalartException(TITLE_NOT_BLANK);
        }
        if (title.length() > MAX_TITLE_LENGTH) {
            throw new MandalartException(INVALID_TITLE_LENGTH);
        }
    }

}

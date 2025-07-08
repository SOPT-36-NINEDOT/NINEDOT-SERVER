package org.sopt36.ninedotserver.mandalart.domain;

import static org.sopt36.ninedotserver.mandalart.exception.CoreGoalErrorCode.INVALID_TITLE_LENGTH;
import static org.sopt36.ninedotserver.mandalart.exception.CoreGoalErrorCode.TITLE_NOT_BLANK;

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
import org.sopt36.ninedotserver.mandalart.exception.CoreGoalException;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CoreGoal extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mandalart_id", nullable = false)
    private Mandalart mandalart;

    @Column(length = 30, nullable = false)
    private String title;

    @Column(nullable = false)
    private int position;

    @Column(nullable = false)
    private boolean aiGeneratable;

    private CoreGoal(Mandalart mandalart, String title, int position, boolean aiGeneratable) {
        validateTitle(title);
        this.mandalart = mandalart;
        this.title = title;
        this.position = position;
        this.aiGeneratable = aiGeneratable;
    }

    public static CoreGoal create(Mandalart mandalart, String title, int position,
        boolean aiGeneratable) {
        return new CoreGoal(mandalart, title, position, aiGeneratable);
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new CoreGoalException(TITLE_NOT_BLANK);
        }
        if (title.length() > 30) {
            throw new CoreGoalException(INVALID_TITLE_LENGTH);
        }
    }
}

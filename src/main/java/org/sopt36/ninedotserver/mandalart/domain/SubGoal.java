package org.sopt36.ninedotserver.mandalart.domain;

import static org.sopt36.ninedotserver.mandalart.exception.SubGoalErrorCode.INVALID_TITLE_LENGTH;
import static org.sopt36.ninedotserver.mandalart.exception.SubGoalErrorCode.TITLE_NOT_BLANK;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt36.ninedotserver.global.entity.BaseEntity;
import org.sopt36.ninedotserver.mandalart.exception.SubGoalException;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class SubGoal extends BaseEntity {

    private static final int MAX_TITLE_LENGTH = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "core_id", nullable = false)
    private CoreGoal coreGoal;

    @Column(length = 30, nullable = false)
    private String title;

    @Column(nullable = false)
    private int position;

    private SubGoal(CoreGoal coreGoal, String title, int position) {
        validateTitle(title);
        this.coreGoal = coreGoal;
        this.title = title;
        this.position = position;
    }

    public static SubGoal create(CoreGoal coreGoal, String title, int position) {
        return new SubGoal(coreGoal, title, position);
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new SubGoalException(TITLE_NOT_BLANK);
        }
        if (title.length() > MAX_TITLE_LENGTH) {
            throw new SubGoalException(INVALID_TITLE_LENGTH);
        }
    }

}

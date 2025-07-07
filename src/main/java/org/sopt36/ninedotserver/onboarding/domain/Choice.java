package org.sopt36.ninedotserver.onboarding.domain;

import static org.sopt36.ninedotserver.onboarding.exception.QuestionErrorCode.CONTENT_NOT_BLANK;
import static org.sopt36.ninedotserver.onboarding.exception.QuestionErrorCode.INVALID_CONTENT_LENGTH;

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
import org.hibernate.annotations.ColumnDefault;
import org.sopt36.ninedotserver.global.entity.BaseEntity;
import org.sopt36.ninedotserver.onboarding.exception.QuestionException;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Choice extends BaseEntity {

    private static final int MAX_CONTENT_LENGTH = 255;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    @ColumnDefault(value = "true")
    private boolean activated;

    private Choice(Question question, String content, boolean activated) {
        validate(content);
        this.question = question;
        this.content = content;
        this.activated = activated;
    }

    public static Choice create(Question question, String content, boolean activated) {
        return new Choice(question, content, activated);
    }

    private void validate(String content) {
        validateContentNotBlank(content);
        validateContentLength(content);
    }

    private void validateContentNotBlank(String content) {
        if (content == null || content.isBlank()) {
            throw new QuestionException(CONTENT_NOT_BLANK);
        }
    }

    private void validateContentLength(String content) {
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new QuestionException(INVALID_CONTENT_LENGTH);
        }
    }
}

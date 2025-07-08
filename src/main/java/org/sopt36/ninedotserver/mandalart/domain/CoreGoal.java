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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt36.ninedotserver.global.entity.BaseEntity;
import org.sopt36.ninedotserver.mandalart.exception.CoreGoalException;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(access = AccessLevel.PROTECTED)
@Table(name = "core_goal")
@Entity
public class CoreGoal extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mandalart_id", nullable = false)
    private Mandalart mandalart;

    @Column(name = "title", length = 30, nullable = false)
    private String title;

    @Column(name = "position", nullable = false)
    private int position;

    @Column(name = "ai_generatable", nullable = false)
    private boolean aiGeneratable;

    private CoreGoal(Mandalart mandalart, String title, int position, boolean aiGeneratable) {
        this.mandalart = mandalart;
        this.title = title;
        this.position = position;
        this.aiGeneratable = aiGeneratable;
    }

    @Builder
    public static CoreGoal create(Mandalart mandalart, String title, int position,
        boolean aiGeneratable) {
        return CoreGoal.builder()
            .mandalart(mandalart)
            .title(title)
            .position(position)
            .aiGeneratable(aiGeneratable)
            .build();
    }

}

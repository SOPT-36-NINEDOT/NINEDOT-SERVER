package org.sopt36.ninedotserver.mandalart.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.sopt36.ninedotserver.entity.BaseEntity;
import org.sopt36.ninedotserver.mandalart.exception.MandalartErrorCode;
import org.sopt36.ninedotserver.mandalart.exception.MandalartException;
import org.sopt36.ninedotserver.user.model.User;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(access = AccessLevel.PROTECTED)
@Table(name = "mandalart")
@DynamicInsert
@Entity
public class Mandalart extends BaseEntity {

    private static final int MAX_TITLE_LENGTH = 30;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", length = MAX_TITLE_LENGTH, nullable = false)
    private String title;

    @Column(name = "ai_generatable", nullable = false)
    @ColumnDefault(value = "true")
    private boolean aiGeneratable;

    @Column(name = "completed_at", nullable = true)
    private Instant completedAt;

    public static Mandalart create(User user, String title, boolean aiGeneratable) {
        return Mandalart.builder()
            .user(user)
            .title(title)
            .aiGeneratable(aiGeneratable)
            .build();
    }

    public void disableAiGeneration() {
        this.aiGeneratable = false;
    }

    public void ensureOwnedBy(Long userId) {
        requireUserId(userId);
        checkOwnership(userId);
    }

    public int getProgressDays(LocalDate today) {
        LocalDate completedDateKst =
            this.completedAt
                .atZone(KST)
                .toLocalDate();

        return (int) ChronoUnit.DAYS.between(completedDateKst, today) + 1;
    }

    public void updateCompletedAt() {
        this.completedAt = Instant.now();
    }

    private void requireUserId(Long userId) {
        if (userId == null) {
            throw new MandalartException(MandalartErrorCode.MANDALART_USER_REQUIRED);
        }
    }

    private void checkOwnership(Long userId) {
        if (!user.isSameId(userId)) {
            throw new MandalartException(MandalartErrorCode.INVALID_MANDALART_USER);
        }
    }
}

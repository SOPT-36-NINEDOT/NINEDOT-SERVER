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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.sopt36.ninedotserver.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(access = AccessLevel.PROTECTED)
@Table(name = "core_goal_snapshot")
@DynamicInsert
@Entity
public class CoreGoalSnapshot extends BaseEntity {

    private static final int MAX_TITLE_LENGTH = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coregoal_id", nullable = false)
    private CoreGoal coreGoal;

    @Column(name = "title", length = MAX_TITLE_LENGTH, nullable = false)
    private String title;

    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    @Column(name = "valid_to")
    private LocalDateTime validTo;

    public static CoreGoalSnapshot create(
        CoreGoal coreGoal,
        String title,
        LocalDateTime validFrom,
        LocalDateTime validTo
    ) {
        return CoreGoalSnapshot.builder()
            .coreGoal(coreGoal)
            .title(title)
            .validFrom(validFrom)
            .validTo(validTo)
            .build();
    }

    public void verifyCoreGoalUser(Long userId) {
        this.coreGoal.verifyUser(userId);
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateValidTo(LocalDateTime now) {
        this.validTo = now;
    }
}

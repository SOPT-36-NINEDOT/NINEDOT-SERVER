package org.sopt36.ninedotserver.mandalart.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(access = AccessLevel.PROTECTED)
@Table(name = "sub_goal_snapshot")
@Entity
public class SubGoalSnapshot {

    private static final int MAX_TITLE_LENGTH = 30;
    private static final int MAX_CYCLE_LENGTH = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subgoal_id", nullable = false)
    private SubGoal subGoal;

    @Column(name = "title", length = MAX_TITLE_LENGTH, nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "cycle", length = MAX_CYCLE_LENGTH, nullable = false)
    private Cycle cycle;

    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    @Column(name = "valid_to")
    private LocalDateTime validTo;

    public static SubGoalSnapshot create(
        SubGoal subGoal,
        String title,
        Cycle cycle,
        LocalDateTime validFrom,
        LocalDateTime validTo
    ) {
        return SubGoalSnapshot.builder()
            .subGoal(subGoal)
            .title(title)
            .cycle(cycle)
            .validFrom(validFrom)
            .validTo(validTo)
            .build();
    }

    public void verifySubGoalUser(Long userId) {
        this.subGoal.verifyUser(userId);
    }

    public void update(String title, Cycle cycle) {
        this.title = title;
        this.cycle = cycle;
    }

}

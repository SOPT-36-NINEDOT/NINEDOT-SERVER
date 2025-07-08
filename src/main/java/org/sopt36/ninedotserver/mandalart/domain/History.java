package org.sopt36.ninedotserver.mandalart.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt36.ninedotserver.global.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class History extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subgoal_id", nullable = false)
    private SubGoal subGoal;

    @Column(nullable = false)
    private LocalDate completedDate;

    private History(SubGoal subGoal, LocalDate completedDate) {
        this.subGoal = subGoal;
        this.completedDate = completedDate;
    }

    public static History create(SubGoal subGoal, LocalDate completedDate) {
        return new History(subGoal, completedDate);
    }
}

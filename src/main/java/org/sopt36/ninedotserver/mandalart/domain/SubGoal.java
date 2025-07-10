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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt36.ninedotserver.global.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(access = AccessLevel.PROTECTED)
@Table(name = "sub_goal")
@Entity
public class SubGoal extends BaseEntity {

    private static final int MAX_TITLE_LENGTH = 30;
    private static final int MAX_CYCLE_LENGTH = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "core_id", nullable = false)
    private CoreGoal coreGoal;

    @Column(name = "title", length = MAX_TITLE_LENGTH, nullable = false)
    private String title;

    @Column(name = "position", nullable = false)
    private int position;

    @Enumerated(EnumType.STRING)
    @Column(name = "cycle", length = MAX_CYCLE_LENGTH, nullable = false)
    private Cycle cycle;

    public static SubGoal create(CoreGoal coreGoal,
        String title,
        int position,
        Cycle cycle
    ) {
        return SubGoal.builder()
            .coreGoal(coreGoal)
            .title(title)
            .position(position)
            .cycle(cycle)
            .build();
    }

    public void verifyUser(Long userId) {
        this.coreGoal.verifyUser(userId);
    }

    public void update(String title, Cycle cycle) {
        this.title = title;
        this.cycle = cycle;
    }

}

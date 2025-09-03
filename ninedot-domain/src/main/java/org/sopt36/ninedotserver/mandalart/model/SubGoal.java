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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt36.ninedotserver.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(access = AccessLevel.PROTECTED)
@Table(name = "sub_goal")
@Entity
public class SubGoal extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "core_id", nullable = false)
    private CoreGoal coreGoal;

    @Column(name = "position", nullable = false)
    private int position;

    public static SubGoal create(
        CoreGoal coreGoal,
        int position
    ) {
        return SubGoal.builder()
            .coreGoal(coreGoal)
            .position(position)
            .build();
    }

    public void verifyUser(Long userId) {
        this.coreGoal.verifyUser(userId);
    }

}

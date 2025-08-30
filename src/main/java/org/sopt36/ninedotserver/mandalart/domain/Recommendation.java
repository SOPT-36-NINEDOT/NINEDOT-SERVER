package org.sopt36.ninedotserver.mandalart.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.sopt36.ninedotserver.global.entity.BaseEntity;
import org.sopt36.ninedotserver.user.domain.User;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(access = AccessLevel.PROTECTED)
@Table(name = "recommendation")
@DynamicInsert
@Entity
public class Recommendation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_goal_snapshot_id", nullable = false)
    private SubGoalSnapshot subGoalSnapshot;

    @Column(name = "recommendation_date", nullable = false)
    private LocalDate recommendationDate;

    public static Recommendation create(
        User user,
        SubGoalSnapshot subGoalSnapshot,
        LocalDate recommendationDate
    ) {
        return Recommendation.builder()
            .user(user)
            .subGoalSnapshot(subGoalSnapshot)
            .recommendationDate(recommendationDate)
            .build();
    }

}

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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.sopt36.ninedotserver.global.entity.BaseEntity;
import org.sopt36.ninedotserver.user.domain.User;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(access = AccessLevel.PROTECTED)
@Table(name = "core_goal")
@DynamicInsert
@Entity
public class CoreGoal extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mandalart_id", nullable = false)
    private Mandalart mandalart;

    @Column(name = "position", nullable = false)
    private int position;

    @Column(name = "ai_generatable", nullable = false)
    @ColumnDefault(value = "true")
    private boolean aiGeneratable;

    public static CoreGoal create(
        Mandalart mandalart,
        int position,
        boolean aiGeneratable
    ) {
        return CoreGoal.builder()
            .mandalart(mandalart)
            .position(position)
            .aiGeneratable(aiGeneratable)
            .build();
    }

    public void verifyUser(Long userId) {
        mandalart.ensureOwnedBy(userId);
    }

    public User getUser() {
        return this.getMandalart().getUser();
    }

    public void disableAiGeneration() {
        this.aiGeneratable = false;
    }

}

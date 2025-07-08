package org.sopt36.ninedotserver.onboarding.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.sopt36.ninedotserver.global.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(access = AccessLevel.PROTECTED)
@Table(name = "question")
@Entity
public class Question extends BaseEntity {

    private static final int MAX_DOMAIN_LENGTH = 20;
    private static final int MAX_TYPE_LENGTH = 10;
    private static final int MAX_CONTENT_LENGTH = 255;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "domain", length = MAX_DOMAIN_LENGTH, nullable = false)
    private Domain domain;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 10, nullable = false)
    private Type type;

    @Column(name = "content", length = MAX_CONTENT_LENGTH, nullable = false)
    private String content;

    @Column(name = "activated", nullable = false)
    @ColumnDefault(value = "true")
    private boolean activated;

    public static Question create(Domain domain, Type type, String content, boolean activated) {
        return Question.builder()
            .domain(domain)
            .type(type)
            .content(content)
            .activated(activated)
            .build();
    }

}

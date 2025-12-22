package org.sopt36.ninedotserver.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.Clock;
import java.time.Instant;
import lombok.Getter;

@MappedSuperclass
public abstract class BaseEntity {

    private static final Clock UTC_CLOCK = Clock.systemUTC();

    @Getter
    @Column(name = "created_at", updatable = false, nullable = false)
    protected Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    protected Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        var now = Instant.now(UTC_CLOCK);
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now(UTC_CLOCK);
    }

}

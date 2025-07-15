package org.sopt36.ninedotserver.mandalart.domain;

import lombok.Getter;

@Getter
public enum Cycle {
    DAILY("매일"),
    WEEKLY("매주"),
    ONCE("한 번만");

    private final String label;

    Cycle(String label) {
        this.label = label;
    }
}

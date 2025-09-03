package org.sopt36.ninedotserver.onboarding.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Type {
    SINGLE_SELECTION("객관식"),
    SUBJECTIVE("주관식");

    private final String label;

    Type(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }
}

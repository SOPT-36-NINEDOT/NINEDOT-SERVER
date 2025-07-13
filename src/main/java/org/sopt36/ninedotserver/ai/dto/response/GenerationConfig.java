package org.sopt36.ninedotserver.ai.dto.response;

import com.fasterxml.jackson.databind.JsonNode;

public record GenerationConfig(
    String responseMimeType,
    JsonNode responseJsonSchema
    , ThinkingConfig thinkingConfig
) {

    public record ThinkingConfig(int thinkingBudget) {

    }
}

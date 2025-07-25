package org.sopt36.ninedotserver.ai.dto.response;

import java.util.List;

public record GenerateContentRequest(List<Content> contents,
                                     GenerationConfig generationConfig) {

    public record Content(List<Part> parts) {

    }

    public record Part(String text) {

    }
}

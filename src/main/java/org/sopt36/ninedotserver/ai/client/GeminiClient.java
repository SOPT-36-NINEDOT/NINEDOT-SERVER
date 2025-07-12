package org.sopt36.ninedotserver.ai.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt36.ninedotserver.ai.dto.response.GenerateContentRequest;
import org.sopt36.ninedotserver.ai.dto.response.GenerationConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiClient implements AiClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.response-schema}")
    private String responseSchema;

    @Override
    public String fetchAiResponse(String prompt) {
        GenerateContentRequest request = buildGeminiRequest(prompt);

        return restClient.post()
                   .body(request)
                   .retrieve()
                   .body(String.class);
    }

    private GenerateContentRequest buildGeminiRequest(String prompt) {
        //ai에 보낼 request 만들어주는 부분
        var contents = List.of(
            new GenerateContentRequest.Content(
                List.of(new GenerateContentRequest.Part(prompt))
            )
        );

        JsonNode schemaNode;
        try {
            schemaNode = objectMapper.readTree(responseSchema);
        } catch (JsonProcessingException e) {
            log.error("responseSchema JSON parsing error", e);
            throw new RuntimeException("Invalid response schema JSON", e);
        }

        GenerationConfig config = new GenerationConfig(
            "application/json",
            schemaNode
            , new GenerationConfig.ThinkingConfig(512)
        );

        return new GenerateContentRequest(contents, config);
    }

}

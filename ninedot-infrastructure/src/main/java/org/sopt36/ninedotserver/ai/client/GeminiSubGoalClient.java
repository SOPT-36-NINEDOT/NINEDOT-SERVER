package org.sopt36.ninedotserver.ai.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.sopt36.ninedotserver.ai.port.AiClient;
import org.sopt36.ninedotserver.ai.dto.external.GenerateContentRequest;
import org.sopt36.ninedotserver.ai.dto.external.GenerateContentRequest.Content;
import org.sopt36.ninedotserver.ai.dto.external.GenerationConfig;
import org.sopt36.ninedotserver.ai.exception.AiException;
import org.sopt36.ninedotserver.ai.exception.AiErrorCode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component("geminiSubgoalClient")
public class GeminiSubGoalClient implements AiClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    private String subGoalResponseSchema;
    @Value("${gemini.api.subgoal-response-schema}")
    private String subGoalResponseSchemaBase64;

    public GeminiSubGoalClient(@Qualifier("geminiRestClient") RestClient restClient,
        ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        this.subGoalResponseSchema = new String(
            Base64.getDecoder().decode(subGoalResponseSchemaBase64), StandardCharsets.UTF_8);
    }

    public String fetchAiResponse(String prompt) {
        GenerateContentRequest request = buildGeminiRequest(prompt);

        try {
            return restClient.post()
                .body(request)
                .retrieve()
                .onStatus(
                    status -> status.is4xxClientError() || status.is5xxServerError(),
                    (req, res) -> {
                        String bodyText;
                        try (InputStream is = res.getBody()) {
                            bodyText = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                        } catch (IOException io) {
                            log.error("Gemini API error reading body", io);
                            bodyText = "<unreadable>";
                        }
                        log.error("Gemini API error: status={}, body={}", res.getStatusCode(),
                            bodyText);
                        throw new AiException(AiErrorCode.AI_API_ERROR);
                    }
                )
                .body(String.class);
        } catch (Exception e) {
            log.error("Failed to fetch AI response", e);
            if (e instanceof AiException) {
                throw e;
            }
            throw new AiException(AiErrorCode.AI_API_CONNECTION_ERROR);
        }
    }

    private GenerateContentRequest buildGeminiRequest(String prompt) {
        List<Content> contents = List.of(
            new Content(List.of(new GenerateContentRequest.Part(prompt)))
        );

        JsonNode schemaNode;
        try {
            schemaNode = objectMapper.readTree(subGoalResponseSchema);
        } catch (JsonProcessingException e) {
            log.error("subGoalResponseSchema JSON parsing error", e);
            throw new RuntimeException("Invalid response schema JSON", e);
        }

        GenerationConfig config = new GenerationConfig(
            "application/json",
            schemaNode,
            new GenerationConfig.ThinkingConfig(0)
        );

        return new GenerateContentRequest(contents, config);
    }
}

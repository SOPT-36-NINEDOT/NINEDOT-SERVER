package org.sopt36.ninedotserver.ai.client;

import static org.sopt36.ninedotserver.ai.exception.AiErrorCode.AI_API_CONNECTION_ERROR;
import static org.sopt36.ninedotserver.ai.exception.AiErrorCode.AI_API_ERROR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.sopt36.ninedotserver.ai.dto.response.GenerateContentRequest;
import org.sopt36.ninedotserver.ai.dto.response.GenerationConfig;
import org.sopt36.ninedotserver.ai.exception.AiException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class GeminiClient implements AiClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    @Value("${gemini.api.response-schema}")
    private String responseSchema;

    public GeminiClient(@Qualifier("geminiRestClient") RestClient restClient,
        ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    @Override
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
                               log.error("Gemini API error: status={}, body={}",
                                   res.getStatusCode(), bodyText);
                               throw new AiException(AI_API_ERROR);
                           }
                       )
                       .body(String.class);
        } catch (Exception e) {
            log.error("Failed to fetch AI response", e);
            if (e instanceof AiException) {
                throw e;
            }
            throw new AiException(AI_API_CONNECTION_ERROR);
        }
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
            schemaNode,
            new GenerationConfig.ThinkingConfig(512)
        );

        return new GenerateContentRequest(contents, config);
    }


}

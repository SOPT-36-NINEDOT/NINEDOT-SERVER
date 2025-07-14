package org.sopt36.ninedotserver.mandalart.controller;

import static org.sopt36.ninedotserver.mandalart.controller.message.MandalartMessage.CREATED_SUCCESS;
import static org.sopt36.ninedotserver.mandalart.controller.message.MandalartMessage.MANDALART_BOARD_RETRIEVED_SUCCESS;
import static org.sopt36.ninedotserver.mandalart.controller.message.MandalartMessage.MANDALART_RETRIEVED_SUCCESS;
import static org.sopt36.ninedotserver.mandalart.controller.message.MandalartMessage.PROGRESS_HISTORY_RETRIEVED_SUCCESS;

import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.global.dto.response.ApiResponse;
import org.sopt36.ninedotserver.mandalart.dto.request.MandalartCreateRequest;
import org.sopt36.ninedotserver.mandalart.dto.response.MandalartBoardResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.MandalartCreateResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.MandalartHistoryResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.MandalartResponse;
import org.sopt36.ninedotserver.mandalart.service.command.MandalartCommandService;
import org.sopt36.ninedotserver.mandalart.service.query.MandalartQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class MandalartController {

    private final MandalartCommandService mandalartCommandService;
    private final MandalartQueryService mandalartQueryService;

    @PostMapping("/mandalarts")
    public ResponseEntity<ApiResponse<MandalartCreateResponse, Void>> createMandalart(
        @Valid @RequestBody MandalartCreateRequest createRequest
    ) {
        // TODO: 로그인 구현 완료 후 토큰에서 userId 획득
        MandalartCreateResponse response = mandalartCommandService.createMandalart(
            1L,
            createRequest
        );
        Long mandalartId = response.id();
        URI location = URI.create("/api/v1/mandalarts/" + mandalartId);

        return ResponseEntity.created(location)
            .body(ApiResponse.created(response, CREATED_SUCCESS));
    }

    @GetMapping("/mandalarts/{mandalartId}/histories")
    public ResponseEntity<ApiResponse<MandalartHistoryResponse, Void>> getMandalartProgressHistory(
        @PathVariable Long mandalartId
    ) {
        Long userId = 1L; // TODO 로그인 구현 완료 후 변경 필요
        MandalartHistoryResponse response = mandalartQueryService.getMandalartHistory(
            userId,
            mandalartId
        );

        return ResponseEntity.ok(ApiResponse.ok(PROGRESS_HISTORY_RETRIEVED_SUCCESS, response));
    }

    @GetMapping("/mandalarts/{mandalartId}")
    public ResponseEntity<ApiResponse<MandalartResponse, Void>> getMandalart(
        @PathVariable Long mandalartId) {
        Long userId = 1L;
        MandalartResponse response = mandalartQueryService.getMandalart(userId, mandalartId);
        return ResponseEntity.ok(ApiResponse.ok(MANDALART_RETRIEVED_SUCCESS, response));
    }

    @GetMapping("/mandalarts/{mandalartId}/board")
    public ResponseEntity<ApiResponse<MandalartBoardResponse, Void>> getMandalartBoard(
        @PathVariable Long mandalartId
    ) {
        Long userId = 1L;
        MandalartBoardResponse response = mandalartQueryService
            .getMandalartBoard(userId, mandalartId);

        return ResponseEntity.ok(ApiResponse.ok(MANDALART_BOARD_RETRIEVED_SUCCESS, response));
    }
}

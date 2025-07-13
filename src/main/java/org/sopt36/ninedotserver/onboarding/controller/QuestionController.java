package org.sopt36.ninedotserver.onboarding.controller;

import static org.sopt36.ninedotserver.onboarding.controller.message.QuestionMessage.JOB_DROPDOWN_RETRIEVED_SUCCESS;
import static org.sopt36.ninedotserver.onboarding.controller.message.QuestionMessage.PERSONA_QUESTION_RETRIEVED_SUCCESS;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.global.dto.response.ApiResponse;
import org.sopt36.ninedotserver.onboarding.dto.response.JobDropdownResponse;
import org.sopt36.ninedotserver.onboarding.dto.response.PersonaQuestionResponse;
import org.sopt36.ninedotserver.onboarding.service.query.QuestionQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class QuestionController {

    private final QuestionQueryService questionQueryService;

    @GetMapping("/persona")
    public ResponseEntity<ApiResponse<PersonaQuestionResponse, Void>> getPersonaQuestions() {
        PersonaQuestionResponse response = questionQueryService.getAllActivatedQuestions();
        return ResponseEntity.ok(ApiResponse.ok(PERSONA_QUESTION_RETRIEVED_SUCCESS, response));
    }

    @GetMapping("/jobs")
    public ResponseEntity<ApiResponse<JobDropdownResponse, Void>> getJobDropdown() {
        JobDropdownResponse response = questionQueryService.getJobDropdown();
        return ResponseEntity.ok(ApiResponse.ok(JOB_DROPDOWN_RETRIEVED_SUCCESS, response));
    }
}

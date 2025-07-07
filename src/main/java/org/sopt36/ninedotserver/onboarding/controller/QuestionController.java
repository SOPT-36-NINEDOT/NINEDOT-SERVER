package org.sopt36.ninedotserver.onboarding.controller;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.onboarding.service.query.QuestionQueryService;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class QuestionController {

    private final QuestionQueryService questionQueryService;

}

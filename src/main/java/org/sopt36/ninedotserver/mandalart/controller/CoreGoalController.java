package org.sopt36.ninedotserver.mandalart.controller;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.mandalart.service.command.CoreGoalCommandService;
import org.sopt36.ninedotserver.mandalart.service.query.CoreGoalQueryService;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CoreGoalController {

    private final CoreGoalCommandService coreGoalCommandService;
    private final CoreGoalQueryService coreGoalQueryService;

}

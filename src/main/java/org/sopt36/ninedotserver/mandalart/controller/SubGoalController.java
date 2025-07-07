package org.sopt36.ninedotserver.mandalart.controller;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.mandalart.service.command.SubGoalCommandService;
import org.sopt36.ninedotserver.mandalart.service.query.SubGoalQueryService;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class SubGoalController {

    private SubGoalCommandService subGoalCommandService;
    private SubGoalQueryService subGoalQueryService;

}

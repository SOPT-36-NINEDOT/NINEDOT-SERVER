package org.sopt36.ninedotserver.mandalart.controller;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.mandalart.service.command.HistoryCommandService;
import org.sopt36.ninedotserver.mandalart.service.query.HistoryQueryService;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class HistoryController {

    private final HistoryCommandService historyCommandService;
    private final HistoryQueryService historyQueryService;


}

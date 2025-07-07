package org.sopt36.ninedotserver.mandalart.controller;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.mandalart.service.command.MandalartCommandService;
import org.sopt36.ninedotserver.mandalart.service.query.MandalartQueryService;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MandalartController {

    private final MandalartCommandService mandalartCommandService;
    private final MandalartQueryService mandalartQueryService;

}

package org.sopt36.ninedotserver.mandalart.service.command;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.mandalart.repository.MandalartRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MandalartCommandService {

    private final MandalartRepository mandalartRepository;
}

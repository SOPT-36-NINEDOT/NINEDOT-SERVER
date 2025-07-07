package org.sopt36.ninedotserver.mandalart.service.command;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.mandalart.repository.HistoryRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class HistoryCommandService {

    private final HistoryRepository historyRepository;
}

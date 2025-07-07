package org.sopt36.ninedotserver.mandalart.service.query;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.mandalart.repository.HistoryRepository;
import org.sopt36.ninedotserver.mandalart.repository.MandalartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class HistoryQueryService {

    private final HistoryRepository historyRepository;
    private final MandalartRepository mandalartRepository;
    
}

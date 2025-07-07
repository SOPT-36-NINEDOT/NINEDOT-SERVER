package org.sopt36.ninedotserver.mandalart.service.query;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.mandalart.repository.CoreGoalRepository;
import org.sopt36.ninedotserver.mandalart.repository.MandalartRepository;
import org.sopt36.ninedotserver.mandalart.repository.SubGoalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SubGoalQueryService {

    private final SubGoalRepository subGoalRepository;
    private final CoreGoalRepository coreGoalRepository;
    private final MandalartRepository mandalartRepository;

}

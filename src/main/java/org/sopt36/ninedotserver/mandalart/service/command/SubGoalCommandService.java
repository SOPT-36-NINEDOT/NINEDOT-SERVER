package org.sopt36.ninedotserver.mandalart.service.command;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.ai.service.AiRecommendationService;
import org.sopt36.ninedotserver.mandalart.repository.CoreGoalRepository;
import org.sopt36.ninedotserver.mandalart.repository.SubGoalRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SubGoalCommandService {

    private final SubGoalRepository subGoalRepository;
    private final CoreGoalRepository coreGoalRepository;
    private final AiRecommendationService aiRecommendationService;
}

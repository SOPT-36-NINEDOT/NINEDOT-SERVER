package org.sopt36.ninedotserver.onboarding.service.query;

import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.onboarding.repository.ChoiceRepository;
import org.sopt36.ninedotserver.onboarding.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class QuestionQueryService {

    private final QuestionRepository questionRepository;
    private final ChoiceRepository choiceRepository;

}

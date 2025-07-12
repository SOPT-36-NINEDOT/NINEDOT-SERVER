package org.sopt36.ninedotserver.onboarding.repository;

import java.util.List;
import org.sopt36.ninedotserver.onboarding.domain.Question;

public interface QuestionRepositoryCustom {
    List<Question> findAllActivated();
}

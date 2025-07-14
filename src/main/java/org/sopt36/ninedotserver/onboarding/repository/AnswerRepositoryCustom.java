package org.sopt36.ninedotserver.onboarding.repository;

import java.util.List;
import java.util.Map;
import org.sopt36.ninedotserver.onboarding.domain.Domain;
import org.sopt36.ninedotserver.onboarding.domain.Question;

public interface AnswerRepositoryCustom {

    List<Question> findQuestionsByUserIdAndDomain(Long userId, Domain domain);
    Map<String, String> findQnAMapByUserId(Long userId);


}

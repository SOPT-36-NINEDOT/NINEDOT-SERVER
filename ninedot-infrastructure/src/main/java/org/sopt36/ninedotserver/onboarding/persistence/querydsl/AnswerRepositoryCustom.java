package org.sopt36.ninedotserver.onboarding.persistence.querydsl;

import java.util.List;
import java.util.Map;
import org.sopt36.ninedotserver.onboarding.model.Domain;
import org.sopt36.ninedotserver.onboarding.model.Question;

public interface AnswerRepositoryCustom {

    List<Question> findQuestionsByUserIdAndDomain(Long userId, Domain domain);

    Map<String, String> findQnAMapByUserId(Long userId);


}

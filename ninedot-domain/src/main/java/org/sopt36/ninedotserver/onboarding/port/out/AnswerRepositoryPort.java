package org.sopt36.ninedotserver.onboarding.port.out;

import java.util.List;
import java.util.Map;
import org.sopt36.ninedotserver.onboarding.model.Answer;
import org.sopt36.ninedotserver.onboarding.model.Domain;
import org.sopt36.ninedotserver.onboarding.model.Question;

public interface AnswerRepositoryPort {

    <S extends Answer> Iterable<S> saveAll(Iterable<S> answers);

    List<Question> findQuestionsByUserIdAndDomain(Long userId, Domain domain);

    List<String> findAllAnswerContentsByUserId(Long userId, Domain domain);

    Map<String, String> findQnAMapByUserId(Long id);
}

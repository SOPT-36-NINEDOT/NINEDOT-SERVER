package org.sopt36.ninedotserver.onboarding.port.out;

import java.util.List;
import java.util.Optional;
import org.sopt36.ninedotserver.onboarding.model.Domain;
import org.sopt36.ninedotserver.onboarding.model.Question;

public interface QuestionRepositoryPort {

    Optional<Question> findById(Long aLong);

    List<Question> findAllByActivatedTrueAndDomain(Domain domain);
}

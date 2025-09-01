package org.sopt36.ninedotserver.onboarding.persistence;

import java.util.List;
import org.sopt36.ninedotserver.onboarding.model.Domain;
import org.sopt36.ninedotserver.onboarding.model.Question;
import org.sopt36.ninedotserver.onboarding.port.out.QuestionRepositoryPort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>, QuestionRepositoryPort {

    List<Question> findAllByActivatedTrueAndDomain(Domain domain);

}

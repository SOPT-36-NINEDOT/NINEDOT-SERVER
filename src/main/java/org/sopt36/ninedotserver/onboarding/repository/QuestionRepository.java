package org.sopt36.ninedotserver.onboarding.repository;

import org.sopt36.ninedotserver.onboarding.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

}

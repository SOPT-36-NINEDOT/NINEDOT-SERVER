package org.sopt36.ninedotserver.onboarding.repository;

import org.sopt36.ninedotserver.onboarding.domain.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

}

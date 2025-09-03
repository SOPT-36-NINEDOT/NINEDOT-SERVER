package org.sopt36.ninedotserver.onboarding.persistence;

import java.util.List;
import org.sopt36.ninedotserver.onboarding.model.Answer;
import org.sopt36.ninedotserver.onboarding.model.Domain;
import org.sopt36.ninedotserver.onboarding.persistence.querydsl.AnswerRepositoryCustom;
import org.sopt36.ninedotserver.onboarding.port.out.AnswerRepositoryPort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long>, AnswerRepositoryPort,
    AnswerRepositoryCustom {

    @Query("SELECT a.content FROM Answer a WHERE a.user.id = :userId AND a.question.domain = :domain")
    List<String> findAllAnswerContentsByUserId(
        @Param("userId") Long userId,
        @Param("domain") Domain domain
    );

}

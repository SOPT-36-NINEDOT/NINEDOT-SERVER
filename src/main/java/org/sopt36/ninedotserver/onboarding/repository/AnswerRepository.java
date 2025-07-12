package org.sopt36.ninedotserver.onboarding.repository;

import java.util.List;
import org.sopt36.ninedotserver.onboarding.domain.Answer;
import org.sopt36.ninedotserver.onboarding.domain.Domain;
import org.sopt36.ninedotserver.onboarding.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long>, AnswerRepositoryCustom {

    @Query("SELECT a.content FROM Answer a WHERE a.user.id = :userId")
    List<String> findAllAnswerContentsByUserId(@Param("userId") Long userId);

}

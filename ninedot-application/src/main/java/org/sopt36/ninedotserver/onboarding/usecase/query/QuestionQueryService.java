package org.sopt36.ninedotserver.onboarding.usecase.query;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

import org.sopt36.ninedotserver.onboarding.dto.response.JobDropdownResponse;
import org.sopt36.ninedotserver.onboarding.dto.response.JobDropdownResponse.Job;
import org.sopt36.ninedotserver.onboarding.dto.response.PersonaQuestionResponse;
import org.sopt36.ninedotserver.onboarding.dto.response.QuestionResponse;
import org.sopt36.ninedotserver.onboarding.exception.QuestionErrorCode;
import org.sopt36.ninedotserver.onboarding.exception.QuestionException;
import org.sopt36.ninedotserver.onboarding.model.Choice;
import org.sopt36.ninedotserver.onboarding.model.Domain;
import org.sopt36.ninedotserver.onboarding.model.Question;
import org.sopt36.ninedotserver.onboarding.port.out.ChoiceRepositoryPort;
import org.sopt36.ninedotserver.onboarding.port.out.QuestionRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class QuestionQueryService {

    private final QuestionRepositoryPort questionRepository;
    private final ChoiceRepositoryPort choiceRepository;

    public PersonaQuestionResponse getAllActivatedQuestions() {
        List<Question> questions = questionRepository.findAllByActivatedTrueAndDomain(
            Domain.PERSONA
        );
        List<Choice> choices = choiceRepository.findAllByActivatedTrue();

        if (questions.isEmpty()) {
            throw new QuestionException(QuestionErrorCode.QUESTION_NOT_FOUND);
        }

        Map<Long, List<Choice>> choiceMap = choices.stream()
            .collect(Collectors.groupingBy(
                c -> c.getQuestion().getId()));

        List<QuestionResponse> responseList = questions.stream()
            .map(q -> QuestionResponse.from(q,
                choiceMap.getOrDefault(q.getId(), List.of())))
            .toList();

        return PersonaQuestionResponse.from(responseList);
    }

    public JobDropdownResponse getJobDropdown() {
        List<Choice> jobChoiceList = choiceRepository.findJobList();
        if (jobChoiceList.isEmpty()) {
            throw new QuestionException(QuestionErrorCode.JOB_DROPDOWN_NOT_FOUND);
        }
        List<Job> jobList = jobChoiceList.stream()
            .map(JobDropdownResponse.Job::from)
            .toList();
        return JobDropdownResponse.of(jobList);
    }
}

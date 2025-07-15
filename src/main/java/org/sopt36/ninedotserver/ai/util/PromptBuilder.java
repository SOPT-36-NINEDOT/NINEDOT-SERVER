package org.sopt36.ninedotserver.ai.util;

import static org.sopt36.ninedotserver.ai.exception.AiErrorCode.SUB_GOAL_PROMPT_PARAMETER_NOT_FOUND;

import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.sopt36.ninedotserver.ai.exception.AiException;

@Slf4j
public class PromptBuilder {

    public static String buildCoreGoalPrompt(int age, String job,

        List<String> questions, List<String> answers,
        String mandalartTitle,
        List<String> coreGoals) {

        if (job == null || questions == null || answers == null ||
            mandalartTitle == null || coreGoals == null) {
            throw new IllegalArgumentException("필수 파라미터가 null입니다");
        }

        if (questions.size() != answers.size()) {
            throw new IllegalArgumentException("질문과 답변의 개수가 일치하지 않습니다"); //근데 이럴 일이 있나..
        }

        StringBuilder sb = new StringBuilder();

        sb.append("나는 ").append(age).append("세 ").append(job).append("이고, 다음은 나를 알 수 있는 질답이야.\n\n");

        for (int i = 0; i < Math.min(questions.size(), answers.size()); i++) {
            sb.append(questions.get(i)).append("\n");
            sb.append(answers.get(i)).append("\n\n");
        }

        sb.append("이런 나의 특징에 기반한 만다라트 계획표를 만드려고 해. "
                + "만다라트에는 전체목표 하나에 8개의 상위목표가 따르고, 상위목표 각각에 하위목표 8개씩 있어. "
                + "이 내용들을 바탕으로 상위목표 8개를 추천해줘. 지금까지 작성한 전체목표, 상위목표는 다음과 같아.\n")

            .append("- 전체목표: ").append(mandalartTitle).append("\n");

        for (int i = 0; i < coreGoals.size(); i++) {
            sb.append(coreGoals.get(i)).append("\n");
        }
        log.info(sb.toString());
        return sb.toString();
    }

    public static String buildSubGoalPrompt(int age, String job,
        String mandalartTitle,
        String coreGoalTitle,
        Map<String, String> questionAnswerMap,
        List<String> existingSubGoals
    ) {
        if (job == null || mandalartTitle == null || coreGoalTitle == null
            || existingSubGoals == null) {
            throw new AiException(SUB_GOAL_PROMPT_PARAMETER_NOT_FOUND);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("나는 ").append(age).append("세 ").append(job).append("이고, 다음은 나를 알 수 있는 질답이야.\n\n");

        // 질문/답변 매핑이 있을 경우에만 프롬프트에 추가
        if (questionAnswerMap != null && !questionAnswerMap.isEmpty()) {
            for (Map.Entry<String, String> entry : questionAnswerMap.entrySet()) {
                String question = entry.getKey();
                String answer = entry.getValue();

                if (answer != null && !answer.isBlank()) {
                    sb.append(question).append("\n");
                    sb.append(answer).append("\n\n");
                }
            }
        }

        sb.append("이런 나의 특징에 기반한 만다라트 계획표를 만드려고 해.\n")
            .append("전체 목표: ").append(mandalartTitle).append("\n")
            .append("상위 목표: ").append(coreGoalTitle).append("\n\n");

        sb.append("내가 이미 작성한 하위 목표는 다음과 같아. 이 목표들과 겹치지 않게 상위 목표에 대한 하위 목표 8개를 추천해줘.\n");
        for (String goal : existingSubGoals) {
            sb.append("- ").append(goal).append("\n");
        }

        sb.append("\n추천해줄 하위 목표는 다음 규칙을 지켜줘:\n")
            .append("1. 반드시 JSON 배열 형식으로 출력해줘.\n")
            .append("2. 각 하위 목표는 반드시 다음 형식을 따라야 해: { \"title\": \"...\", \"cycle\": \"...\" }\n")
            .append("3. title은 한 줄 요약으로 30자를 넘지 않아야 해.\n")
            .append("4. cycle은 반드시 아래 중 하나로만 설정해줘:\n")
            .append("   - \"매일\"\n")
            .append("   - \"매주\"\n")
            .append("   - \"한 번만\"\n")
            .append("5. 8개의 추천 목록들에 해당하는 cycle의 빈도수는 매일 3개, 매주 2개, 한 번만 3개야.")
            .append("6. 내가 이미 쓴 하위 목표들과 겹치지 않아야 해.\n")
            .append("7. 총 8개의 하위 목표를 추천해줘.\n")
            .append("8. 다른 텍스트는 절대 포함하지 말고 JSON 배열만 응답해줘.\n");

        String prompt = sb.toString();
        log.info("[SubGoalPrompt]\n{}", prompt);
        return prompt;

    }

}


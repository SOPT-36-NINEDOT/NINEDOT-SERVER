package org.sopt36.ninedotserver.ai.util;

import java.util.List;

public class PromptBuilder {

    public static String buildCoreGoalPrompt(int age, String job,
        List<String> questions, List<String> answers,
        String mandalartTitle,
        String core1, String core2) {

        if (job == null || questions == null || answers == null ||
                mandalartTitle == null || core1 == null || core2 == null) {
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

            .append("- 전체목표: ").append(mandalartTitle).append("\n")
            .append("    - 상위목표1: ").append(core1).append("\n")
            .append("    - 상위목표2: ").append(core2).append("\n");
        return sb.toString();
    }
}


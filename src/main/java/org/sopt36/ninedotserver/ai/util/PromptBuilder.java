package org.sopt36.ninedotserver.ai.util;

public class PromptBuilder {

    public static String buildCoreGoalPrompt(int age, String job,
        String question1, String answer1,
        String question2, String answer2,
        String question3, String answer3,
        String mandalartTitle,
        String core1, String core2) {
        StringBuilder sb = new StringBuilder();

        sb.append("나는 ").append(age).append("세 ").append(job).append("이고, 다음은 나를 알 수 있는 질답이야.\n\n")

            .append(question1).append("\n")
            .append(answer1).append("\n\n")
            .append(question2).append("\n")
            .append(answer2).append("\n\n")
            .append(question3).append("\n")
            .append(answer3).append("\n\n")

            .append("이런 나의 특징에 기반한 만다라트 계획표를 만드려고 해. 만다라트에는 전체목표 하나에 8개의 상위목표가 따르고,\n")
            .append("상위목표 각각에 하위목표 8개씩 있어. 아직 채우지 못한 상위목표를 마저 채워줘.\n")
            .append("지금까지 작성한 전체목표, 상위목표는 다음과 같아.\n")

            .append("- 전체목표: ").append(mandalartTitle).append("\n")
            .append("    - 상위목표1: ").append(core1).append("\n")
            .append("    - 상위목표2: ").append(core2).append("\n");
        return sb.toString();
    }
}

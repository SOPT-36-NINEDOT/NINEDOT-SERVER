package org.sopt36.ninedotserver.onboarding.domain;

import static org.sopt36.ninedotserver.onboarding.exception.ChoiceErrorCode.CHOICE_NOT_FOUND;

import java.util.Arrays;
import lombok.Getter;
import org.sopt36.ninedotserver.onboarding.exception.ChoiceException;

@Getter
public enum ChoiceInfo {
    DETAILED_PLAN(1, "구체적인 실행 계획이 필요해요", "구체적 실행 계획 필요"),
    EMOTIONAL_MOTIVATION(2, "감성적 동기부여가 중요해요", "감성적 동기부여 중요"),
    SIMPLE_TASKS(3, "간단한 할 일 쌓아가는 게 좋아요", "간단한 할 일 쌓기"),
    SCHEDULE_BASED(4, "일정 기반으로 진행하는 걸 좋아해요", "일정 기반 진행 선호"),
    NONE(5, "해당없음", "해당없음"),
    ALONE(6, "혼자 조용히 하는 게 편해요", "혼자 조용히 선호"),
    WITH_OTHERS(7, "누군가와 함께 하면 더 힘이 나요", "함께 하면 동기 강화"),
    BOTH(8, "둘 다 비슷해요", "둘 다 비슷"),
    PROCRASTINATION(9, "의지는 있지만 자꾸 미뤄요", "의지 있지만 미룸"),
    LOST_MOTIVATION(10, "중간에 동기 부여가 떨어져요", "중간 동기 저하"),
    NO_EXTERNAL_STIMULUS(11, "외부 자극이 없으면 움직이지 않아요", "외부 자극 필요"),
    TIME_MANAGEMENT(12, "시간 관리가 잘 안 돼요", "시간 관리 어려움"),
    NOTHING(13, "해당없음", "해당없음");

    private final int id;
    private final String fullSentence;
    private final String shortSentence;

    ChoiceInfo(int id, String fullSentence, String shortSentence) {
        this.id = id;
        this.fullSentence = fullSentence;
        this.shortSentence = shortSentence;
    }

    public static String getShortSentenceById(int id) {
        return Arrays.stream(ChoiceInfo.values())
            .filter(choice -> choice.getId() == id)
            .findFirst()
            .orElseThrow(() -> new ChoiceException(CHOICE_NOT_FOUND))
            .getShortSentence();
    }
}

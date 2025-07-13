package org.sopt36.ninedotserver.mandalart.dto.response;

import java.util.List;

public record StreakResponse(
    int streakDay,
    int completedTodoCount,
    List<TodoResponse> completedTodos
) {

}

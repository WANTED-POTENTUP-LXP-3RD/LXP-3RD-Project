package com.lxp.aplus.progress.presentation.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lxp.aplus.progress.application.result.LearningHistoryResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LearningHistoryResponse(
        Long enrollmentId,
        int overallProgressRate,
        Long lastWatchedVideoId,
        Integer lastWatchedDurationOfLastVideo,
        LocalDateTime lastWatchedAt,
        List<LectureProgressResponse> lectureProgresses
) {
    public static LearningHistoryResponse from(LearningHistoryResult result) {
        List<LectureProgressResponse> lectureProgresses = result.lectureProgresses().stream()
                .map(LectureProgressResponse::from)
                .collect(Collectors.toList());

        return new LearningHistoryResponse(
                result.enrollmentId(),
                result.overallProgressRate(),
                result.lastWatchedVideoId(),
                result.lastWatchedDurationOfLastVideo(),
                result.lastWatchedAt(),
                lectureProgresses
        );
    }
}

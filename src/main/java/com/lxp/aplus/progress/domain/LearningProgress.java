package com.lxp.aplus.progress.domain;

import com.lxp.aplus.progress.application.port.LectureSummaryDto;
import com.lxp.aplus.progress.presentation.response.LectureProgressResponse;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class LearningProgress {

    private final List<Progress> progresses;

    public LearningProgress(List<Progress> progresses) {
        this.progresses = progresses;
    }
//TODO  진도율 계산 방법 통일해야함
    public int calculateOverallProgressRate(long totalLectureCount) {
        if (totalLectureCount == 0) {
            return 0;
        }
        long completedCount = progresses.stream().filter(Progress::isCompleted).count();
        return (int) ((double) completedCount / totalLectureCount * 100);
    }

    public Optional<Progress> findLastWatchedProgress() {
        return progresses.stream()
                .filter(p -> p.getLastWatchedAt() != null)
                .max(Comparator.comparing(Progress::getLastWatchedAt));
    }

    public List<LectureProgressResponse> mapToLectureProgressResponses(List<LectureSummaryDto> lectureDetails) {
        Map<Long, Progress> progressMap = progresses.stream()
                .collect(Collectors.toMap(Progress::getLectureResourceId, Function.identity()));

        return lectureDetails.stream()
                .map(lecture -> {
                    Progress progress = progressMap.get(lecture.resourceId());
                    return createLectureProgressResponse(lecture, progress);
                })
                .toList();
    }

    private LectureProgressResponse createLectureProgressResponse(LectureSummaryDto lecture, Progress progress) {
        if (progress == null) {
            return new LectureProgressResponse(
                    lecture.resourceId(),
                    lecture.title(),
                    0,
                    lecture.totalDurationSeconds(),
                    false,
                    null
            );
        }
        return new LectureProgressResponse(
                lecture.resourceId(),
                lecture.title(),
                progress.getWatchedDuration(),
                lecture.totalDurationSeconds(),
                progress.isCompleted(),
                progress.getLastWatchedAt()
        );
    }
}

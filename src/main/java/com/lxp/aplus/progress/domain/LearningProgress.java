package com.lxp.aplus.progress.domain;

import com.lxp.aplus.course.domain.ResourceType;
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

    private final Long enrollmentId;
    private final List<Progress> progresses;

    public LearningProgress(Long enrollmentId, List<Progress> progresses) {
        this.enrollmentId = enrollmentId;
        this.progresses = progresses;
    }

    /**
     * 전체 진도율을 계산한다.
     * - VIDEO: (시청 시간 / 전체 길이) × 100
     * - FILE: 완료 시 100%, 미완료 시 0%
     * - 전체: 각 리소스 진도율의 평균 (반올림)
     *
     * @param lectureDetails 리소스 목록 (resourceType 포함)
     * @return 전체 진도율 (0~100)
     */
    public int calculateOverallProgressRate(List<LectureSummaryDto> lectureDetails) {
        if (lectureDetails.isEmpty()) {
            return 0;
        }

        Map<Long, Progress> progressMap = progresses.stream()
                .collect(Collectors.toMap(
                        Progress::getLectureResourceId,
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        double average = lectureDetails.stream()
                .mapToInt(lecture -> calculateResourceRate(lecture, progressMap.get(lecture.resourceId())))
                .average()
                .orElse(0);

        return (int) Math.round(average);
    }

    /**
     * 개별 리소스의 진도율을 계산한다.
     *
     * @param lecture 리소스 정보 (타입, 전체 길이 포함)
     * @param progress 해당 리소스의 Progress (없으면 null)
     * @return 리소스 진도율 (0~100)
     */
    private int calculateResourceRate(LectureSummaryDto lecture, Progress progress) {
        // VIDEO 타입: 시청 비율 계산
        if (lecture.resourceType() == ResourceType.VIDEO) {
            int total = lecture.totalDurationSeconds();
            int watched = (progress == null) ? 0 : progress.getWatchedDuration();

            if (total == 0) {
                return (progress != null && progress.isCompleted()) ? 100 : 0;
            }

            int rate = (int) Math.round((double) watched / total * 100);
            return Math.min(rate, 100);  // 100% 초과 방지
        }

        // FILE 타입: 완료 여부만 판단 (0% 또는 100%)
        boolean completed = (progress != null) && progress.isCompleted();
        return completed ? 100 : 0;
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

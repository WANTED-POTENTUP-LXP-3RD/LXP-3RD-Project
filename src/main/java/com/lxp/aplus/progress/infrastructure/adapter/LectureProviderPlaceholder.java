package com.lxp.aplus.progress.infrastructure.adapter;

import com.lxp.aplus.progress.application.port.LectureSummaryDto;
import com.lxp.aplus.progress.application.port.LectureDurationDto;
import com.lxp.aplus.progress.application.port.LectureProvider;
import org.springframework.stereotype.Component;

import java.util.List;
//TODO 나중에 course 쪽에서 LectureProvider 구현체 만들어주면 그거 사용하고 이 파일 삭제 예정 (1/14일)
@Component
public class LectureProviderPlaceholder implements LectureProvider {

    private static final int FAKE_TOTAL_DURATION_SECONDS = 300;


    @Override
    public LectureDurationDto getLectureInfo(Long lectureResourceId) {
        return new LectureDurationDto(FAKE_TOTAL_DURATION_SECONDS);
    }


    @Override
    public List<LectureSummaryDto> getLectureDetailsByCourseId(Long courseId) {
        return List.of(
                new LectureSummaryDto(1L, "Placeholder Lecture 1", FAKE_TOTAL_DURATION_SECONDS),
                new LectureSummaryDto(2L, "Placeholder Lecture 2", FAKE_TOTAL_DURATION_SECONDS + 120),
                new LectureSummaryDto(3L, "Placeholder Lecture 3", FAKE_TOTAL_DURATION_SECONDS - 60)
        );
    }
}

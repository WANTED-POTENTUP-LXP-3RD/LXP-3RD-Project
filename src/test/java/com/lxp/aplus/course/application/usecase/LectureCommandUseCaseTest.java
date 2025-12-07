package com.lxp.aplus.course.application.usecase;

import com.lxp.aplus.course.application.command.CreateLectureCommand;
import com.lxp.aplus.course.application.result.LectureResult;
import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.course.domain.CourseRepository;
import com.lxp.aplus.course.domain.Lecture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("LectureCommandUseCase 테스트")
class LectureCommandUseCaseTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    Course course;

    @InjectMocks
    private LectureCommandUseCase lectureCommandUseCase;

    @Test
    @DisplayName("강의를 생성할 수 있다")
    void createLecture() {
        // given
        Long courseId = 1L;
        Long sectionId = 1L;

        CreateLectureCommand command = CreateLectureCommand.builder()
                .title("1. 강좌 소개")
                .description("강좌 전체 흐름과 기대 결과를 안내합니다.")
                .build();

        Lecture fakeLecture = new Lecture(
                    null,
                "1. 강좌 소개",
                "강좌 전체 흐름과 기대 결과를 안내합니다.",
                1
        );

        // CourseRepository가 Course 반환하도록 stub
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        // Course.addLectureToSection 이 fakeLecture를 반환하도록 stub
        when(course.addLectureToSection(sectionId, command.title(), command.description()))
                .thenReturn(fakeLecture);

        // when
        LectureResult result = lectureCommandUseCase.createLecture(courseId, sectionId, command);

        // then
        // 1) 레포/도메인에 메시지가 제대로 갔는지
        verify(courseRepository).findById(courseId);
        verify(course).addLectureToSection(sectionId, command.title(), command.description());
        verify(courseRepository).save(course);

        // 2) 결과 매핑 검증
        assertThat(result.id()).isEqualTo(fakeLecture.getId());
        assertThat(result.title()).isEqualTo(fakeLecture.getTitle());
        assertThat(result.description()).isEqualTo(fakeLecture.getDescription());
        assertThat(result.orderIndex()).isEqualTo(1);
    }
}
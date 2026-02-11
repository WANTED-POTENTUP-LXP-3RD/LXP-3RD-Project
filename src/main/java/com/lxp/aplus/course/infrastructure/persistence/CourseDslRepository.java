package com.lxp.aplus.course.infrastructure.persistence;

import com.lxp.aplus.course.application.port.in.dto.ResourceSummary;
import static com.lxp.aplus.course.domain.QCourse.course;
import static com.lxp.aplus.course.domain.QLecture.lecture;
import static com.lxp.aplus.course.domain.QLectureResourceV2.lectureResourceV2;
import static com.lxp.aplus.course.domain.QSection.section;

import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.course.domain.Lecture;
import com.lxp.aplus.course.infrastructure.persistence.expressions.CourseExpressions;
import com.lxp.aplus.course.infrastructure.persistence.expressions.LectureExpressions;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CourseDslRepository {
    private final JPAQueryFactory queryFactory;

    public List<ResourceSummary> findLectureSummariesByCourseId(Long courseId) {
        return queryFactory
                .select(Projections.constructor(ResourceSummary.class,
                        lectureResourceV2.id,
                        lecture.title,
                        lecture.totalDurationSeconds
                ))
                .from(lecture)
                .join(lecture.section, section)
                .join(lecture.lectureResources, lectureResourceV2)
                .where(
                        LectureExpressions.courseIdEq(courseId),
                        LectureExpressions.isVideoResource()
                )
                .orderBy(
                        section.orderIndex.asc(),
                        lecture.orderIndex.asc()
                )
                .fetch();
    }

    public Optional<Course> findWithCurriculumById(Long courseId) {
        Course result = queryFactory
                .selectFrom(course)
                .distinct()
                .leftJoin(course.sections, section).fetchJoin()
                .where(CourseExpressions.courseIdEq(courseId))
                .fetchOne();
        return Optional.ofNullable(result);
    }

    public Optional<Course> findPublishedWithCurriculumById(Long courseId) {
        Course result = queryFactory
                .selectFrom(course)
                .distinct()
                .leftJoin(course.sections, section).fetchJoin()
                .where(
                        CourseExpressions.courseIdEq(courseId),
                        CourseExpressions.isPublished()
                )
                .fetchOne();
        return Optional.ofNullable(result);
    }

    public int countLecturesByCourseId(Long courseId) {
        Long count = queryFactory
                .select(lecture.count())
                .from(lecture)
                .innerJoin(lecture.section, section)
                .where(LectureExpressions.courseIdEq(courseId))
                .fetchOne();

        return count != null ? count.intValue() : 0;
    }

    public List<Lecture> findAllLecturesWithResourcesByCourseId(Long courseId) {
        return queryFactory
                .selectFrom(lecture)
                .distinct()
                .leftJoin(lecture.lectureResources, lectureResourceV2).fetchJoin()
                .innerJoin(lecture.section, section)
                .where(LectureExpressions.courseIdEq(courseId))
                .fetch();
    }
}

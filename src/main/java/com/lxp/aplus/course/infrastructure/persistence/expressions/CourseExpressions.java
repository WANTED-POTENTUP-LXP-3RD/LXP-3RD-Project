package com.lxp.aplus.course.infrastructure.persistence.expressions;

import com.lxp.aplus.course.domain.CourseStatus;
import static com.lxp.aplus.course.domain.QCourse.course;
import com.querydsl.core.types.dsl.BooleanExpression;

public class CourseExpressions {

    public static BooleanExpression courseIdEq(Long courseId) {
        return courseId != null ? course.id.eq(courseId) : null;
    }

    public static BooleanExpression isPublished() {
        return course.courseStatus.eq(CourseStatus.PUBLISHED);
    }
}

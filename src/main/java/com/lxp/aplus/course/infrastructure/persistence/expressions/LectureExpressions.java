package com.lxp.aplus.course.infrastructure.persistence.expressions;

import static com.lxp.aplus.course.domain.QLectureResourceV2.lectureResourceV2;
import static com.lxp.aplus.course.domain.QSection.section;

import com.lxp.aplus.course.domain.ResourceType;
import com.querydsl.core.types.dsl.BooleanExpression;

public class LectureExpressions {
    public static BooleanExpression courseIdEq(Long courseId) {
        return courseId != null ? section.course.id.eq(courseId) : null;
    }

    public static BooleanExpression isVideoResource() {
        return lectureResourceV2.resourceType.eq(ResourceType.VIDEO);
    }
}

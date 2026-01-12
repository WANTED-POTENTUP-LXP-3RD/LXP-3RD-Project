package com.lxp.aplus.review.application.policy;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.CourseErrorCode;
import com.lxp.aplus.common.error.code.ReviewErrorCode;
import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.course.domain.CourseStatus;
import com.lxp.aplus.review.application.port.out.CourseQueryPort;
import com.lxp.aplus.review.application.port.out.EnrollmentQueryPort;
import com.lxp.aplus.review.domain.Reviews;
import com.lxp.aplus.review.domain.ReviewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewBusinessPolicy {
    private final ReviewsRepository reviewRepository;
    private final EnrollmentQueryPort enrollmentQueryPort;
    private final CourseQueryPort courseQueryPort;

    //생성 검증
    public void validateCreateReview(long userId, long courseId) {
        validateCourse(userId, courseId);
        validateEnrolled(userId, courseId);
        validateAlreadyReviewed(userId, courseId);
    }

    //업데이트 검증
    public void validateUpdateReview(long userId, Reviews review) {
        validateCourse(userId, review.getCourseId());
        validateEnrolled(userId, review.getCourseId());
    }

    //중복 리뷰 검증
    public void validateAlreadyReviewed(Long userId, Long courseId) {
        if (reviewRepository.existsOwnReviewInCourse(userId, courseId)) {
            throw new BusinessException(ReviewErrorCode.ALREADY_REGISTER_IN_COURSE);
        }
    }

    //수강중인 강좌 검증
    public void validateEnrolled(Long userId, Long courseId) {
        if (!enrollmentQueryPort.existsEnrollment(userId, courseId)) {
            throw new BusinessException(ReviewErrorCode.CANT_REVIEW_IN_NOT_ENROLLED);
        }
    }

    //강좌 검증
    public void validateCourse(Long userId, Long courseId) {
        Course course = courseQueryPort.findCourse(courseId)
                .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));

        validateCourseIsPublished(course.getCourseStatus());
        validateOwnCourse(userId, course.getId());
    }

    //강좌 소유자 검증
    private void validateOwnCourse(Long userId, Long instructorId) {
        if (instructorId.equals(userId)) {
            throw new BusinessException(ReviewErrorCode.CANT_REVIEW_IN_OWN_COURSE);
        }
    }

    //강좌 발행상태 검증
    private void validateCourseIsPublished(CourseStatus status){
        if(status!=CourseStatus.PUBLISHED){
            throw new BusinessException(ReviewErrorCode.COURSE_IS_NOT_PUBLISHED);
        }
    }
}

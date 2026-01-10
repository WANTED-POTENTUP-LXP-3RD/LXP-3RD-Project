package com.lxp.aplus.review.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.CourseErrorCode;
import com.lxp.aplus.common.error.code.ReviewErrorCode;
import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.review.application.command.ReviewCreateCommand;
import com.lxp.aplus.review.application.port.out.CourseQueryPort;
import com.lxp.aplus.review.application.port.out.EnrollmentQueryPort;
import com.lxp.aplus.review.application.result.ReviewUpsertResult;
import com.lxp.aplus.review.domain.Review;
import com.lxp.aplus.review.domain.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewCommandUseCase {
    private final ReviewRepository reviewRepository;
    private final EnrollmentQueryPort enrollmentQueryPort;
    private final CourseQueryPort courseQueryPort;

    /**
     * 새로운 리뷰를 등록합니다.
     *
     * @param command 리뷰 등록에 필요한 데이터 (courseId, userId, rating, content)
     * @return 등록된 리뷰 결과 DTO
     */
    public ReviewUpsertResult createReview(ReviewCreateCommand command) {
        validateCreateReview(command.userId(), command.courseId());

        Review savedReview = reviewRepository.save(command.toEntity());

        return ReviewUpsertResult.from(savedReview);
    }

    //생성시 검증
    private void validateCreateReview(long userId, long courseId) {
        validateOwnCourse(userId, courseId);
        validateEnrolled(userId, courseId);
        validateAlreadyReviewed(userId, courseId);
    }

    //강좌 소유자 검증
    private void validateOwnCourse(Long userId, Long courseId) {
        Course course = courseQueryPort.findCourse(courseId)
                .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));

        if (course.getInstructorId().equals(userId)) {
            throw new BusinessException(ReviewErrorCode.CANT_REVIEW_IN_OWN_COURSE);
        }
    }

    //중복 리뷰 검증
    private void validateAlreadyReviewed(Long userId, Long courseId) {
        if (reviewRepository.existsOwnReviewInCourse(userId, courseId)) {
            throw new BusinessException(ReviewErrorCode.ALREADY_REGISTER_IN_COURSE);
        }
    }

    //수강중인 강좌 검증
    private void validateEnrolled(Long userId, Long courseId) {
        if (!enrollmentQueryPort.existsEnrollment(userId, courseId)) {
            throw new BusinessException(ReviewErrorCode.CANT_REVIEW_IN_NOT_ENROLLED);
        }
    }
}

package com.lxp.aplus.review.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.CourseErrorCode;
import com.lxp.aplus.common.error.code.ReviewErrorCode;
import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.course.domain.CourseRepository;
import com.lxp.aplus.enrollment.domain.EnrollmentRepository;
import com.lxp.aplus.review.application.command.BaseReviewCommand;
import com.lxp.aplus.review.application.command.ReviewCreateCommand;
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
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    /**
     * 새로운 리뷰를 등록합니다.
     *
     * @param command 리뷰 등록에 필요한 데이터 (courseId, userId, rating, content)
     * @return 등록된 리뷰 결과 DTO
     */
    public ReviewUpsertResult createReview(ReviewCreateCommand command) {
        validateOwnCourse(command);
        validateEnrolled(command);

        Review review = Review.builder()
                .courseId(command.courseId())
                .userId(command.userId())
                .rating(command.rating())
                .content(command.content())
                .build();
        Review savedReview = reviewRepository.save(review);

        return ReviewUpsertResult.from(savedReview);
    }

    private void validateOwnCourse(BaseReviewCommand command) {
        Course course = courseRepository.findById(command.courseId())
                .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));

        if (course.getInstructorId().equals(command.userId())) {
            throw new BusinessException(ReviewErrorCode.CANT_REVIEW_IN_OWN_COURSE);
        }
    }

    private void validateEnrolled(BaseReviewCommand command) {
        if(!enrollmentRepository.existsByStudentIdAndCourseId(command.userId(), command.courseId())) {
            throw new BusinessException(ReviewErrorCode.CANT_REVIEW_IN_NOT_ENROLLED);
        }
    }
}

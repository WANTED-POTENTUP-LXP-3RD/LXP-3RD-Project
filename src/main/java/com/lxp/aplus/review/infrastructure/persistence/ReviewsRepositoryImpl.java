package com.lxp.aplus.review.infrastructure.persistence;

import com.lxp.aplus.review.domain.Reviews;
import com.lxp.aplus.review.domain.ReviewsRepository;
import com.lxp.aplus.review.domain.constant.ReviewStatus;
import com.lxp.aplus.review.infrastructure.dto.ReviewWroteDto;
import com.lxp.aplus.review.infrastructure.persistence.dto.ReviewAnalyzeItem;
import com.lxp.aplus.review.infrastructure.persistence.dto.ReviewSummary;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewsRepositoryImpl implements ReviewsRepository {
    private final ReviewsJpaRepository reviewJpaRepository;

    @Override
    public Reviews save(Reviews review) {
        return reviewJpaRepository.save(review);
    }

    @Override
    public Boolean existsOwnReviewInCourse(Long userId, Long courseId) {
        return reviewJpaRepository.existsByUserIdAndCourseId(userId, courseId);
    }

    @Override
    public Optional<Reviews> getReview(Long userId, Long courseId) {
        return reviewJpaRepository.findByUserIdAndCourseIdAndStatus(userId, courseId, ReviewStatus.DISPLAY);
    }

    @Override
    public Slice<Reviews> getCourseReviews(Long courseId, Pageable pageable) {
        return reviewJpaRepository.findByCourseIdAndStatus(courseId, ReviewStatus.DISPLAY, pageable);
    }

    @Override
    public Integer countCourseReviews(Long courseId) {
        return reviewJpaRepository.countByCourseId(courseId);
    }

    @Override
    public void deleteReview(Reviews review) {
        reviewJpaRepository.delete(review);
    }

    @Override
    public List<ReviewWroteDto> checkReviewedByCourseIds(List<Long> courseIds, Long userId) {
        return reviewJpaRepository.findByUserIdAndCourseIdIn(userId, courseIds).stream().map(ReviewWroteDto::from).toList();
    }

    @Override
    public List<ReviewSummary> getReviewInfoInCourse(List<Long> courseId) {
        return reviewJpaRepository.getReviewInfosInCourse(courseId);
    }

    @Override
    public List<ReviewAnalyzeItem> findAnalyzeItems(Long courseId, LocalDateTime from) {
        return reviewJpaRepository.findAnalyzeItems(courseId, from);
    }
}

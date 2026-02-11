package com.lxp.aplus.review.infrastructure.adapter;

import com.lxp.aplus.review.application.command.ReviewAnalyzeSaveCommand;
import com.lxp.aplus.review.application.port.out.ReviewAnalyzeSavePort;
import com.lxp.aplus.review.domain.ReviewsAnalysis;
import com.lxp.aplus.review.infrastructure.persistence.ReviewsAnalysisJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewAnalyzeSaveAdapter implements ReviewAnalyzeSavePort {
    private final ReviewsAnalysisJpaRepository repository;

    @Override
    public void save(ReviewAnalyzeSaveCommand command) {
        ReviewsAnalysis entity = ReviewsAnalysis.create(
                command.courseId(),
                command.day(),
                command.mood(),
                command.insightSummary()
        );
        repository.save(entity);
    }
}

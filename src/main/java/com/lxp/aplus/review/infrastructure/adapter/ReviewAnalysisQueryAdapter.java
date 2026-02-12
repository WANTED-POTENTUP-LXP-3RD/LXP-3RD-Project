package com.lxp.aplus.review.infrastructure.adapter;

import com.lxp.aplus.review.application.port.out.ReviewAnalysisQueryPort;
import com.lxp.aplus.review.infrastructure.persistence.ReviewsJpaRepository;
import com.lxp.aplus.review.infrastructure.persistence.dto.ReviewAnalyzeItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReviewAnalysisQueryAdapter implements ReviewAnalysisQueryPort {
    private final ReviewsJpaRepository reviewJpaRepository;

    @Override
    public List<ReviewAnalyzeItem> findAnalyzeItems(Long courseId, LocalDateTime from) {
        return reviewJpaRepository.findAnalyzeItems(courseId, from);
    }
}

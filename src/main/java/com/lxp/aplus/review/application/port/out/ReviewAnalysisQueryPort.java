package com.lxp.aplus.review.application.port.out;

import com.lxp.aplus.review.infrastructure.persistence.dto.ReviewAnalyzeItem;

import java.time.LocalDateTime;
import java.util.List;

public interface ReviewAnalysisQueryPort {
    List<ReviewAnalyzeItem> findAnalyzeItems(Long courseId, LocalDateTime from);
}

package com.lxp.aplus.review.application.port.out;

import com.lxp.aplus.review.application.result.ReviewAnalysisAiResult;
import com.lxp.aplus.review.infrastructure.persistence.dto.ReviewAnalyzeItem;

import java.util.List;

public interface ReviewAnalysisAiPort {
    ReviewAnalysisAiResult analyze(List<ReviewAnalyzeItem> reviews);
}

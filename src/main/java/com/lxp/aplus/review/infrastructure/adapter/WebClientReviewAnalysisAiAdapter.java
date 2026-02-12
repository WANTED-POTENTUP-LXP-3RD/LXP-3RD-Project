package com.lxp.aplus.review.infrastructure.adapter;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.ReviewErrorCode;
import com.lxp.aplus.review.application.port.out.ReviewAnalysisAiPort;
import com.lxp.aplus.review.application.result.ReviewAnalysisAiResult;
import com.lxp.aplus.review.infrastructure.persistence.dto.ReviewAnalyzeItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
@Slf4j
@Component
@RequiredArgsConstructor
public class WebClientReviewAnalysisAiAdapter implements ReviewAnalysisAiPort {

    private final WebClient webClient;

    @Value("${ai.review.base-url}")
    private String aiBaseUrl;

    @Override
    public ReviewAnalysisAiResult analyze(List<ReviewAnalyzeItem> reviews) {
        ReviewAnalyzeAiRequest body = ReviewAnalyzeAiRequest.from(reviews);

        try {
            log.info("AI base-url={}", aiBaseUrl);
            ReviewAnalyzeAiResponse res = webClient.post()
                    .uri(aiBaseUrl + "/internal/review/analyze")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(ReviewAnalyzeAiResponse.class)
                    .timeout(Duration.ofSeconds(3)) // ✅ MVP 타임아웃(너희 정책값으로)
                    .block();

            if (res == null) {
                throw new BusinessException(ReviewErrorCode.AI_ANALYZE_FAILED);
            }

            return new ReviewAnalysisAiResult(res.mood(), res.insightSummary());

        } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
            // Python이 응답은 했는데 4xx/5xx
            log.warn("AI responded status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new BusinessException(ReviewErrorCode.AI_ANALYZE_FAILED); // (정책에 맞게)

        } catch (Exception e) {
            e.printStackTrace();
            // retrieve() 에러 포함해서 일단 503로 매핑
            throw new BusinessException(ReviewErrorCode.AI_UNAVAILABLE);
        }
    }

    public record ReviewAnalyzeAiRequest(List<Item> reviews) {
        public static ReviewAnalyzeAiRequest from(List<ReviewAnalyzeItem> items) {
            List<Item> mapped = items.stream()
                    .map(i -> new Item(normalizeRating(i.rating()), i.content()))
                    .toList();
            return new ReviewAnalyzeAiRequest(mapped);
        }

        private static int normalizeRating(Integer raw) {
            if (raw == null) {
                return 3; // 중립 기본값
            }

            // 이미 1~5
            if (raw >= 1 && raw <= 5) {
                return raw;
            }

            // 10단위 점수 → 5점 척도
            if (raw % 10 == 0) {
                int scaled = raw / 10;
                if (scaled >= 1 && scaled <= 5) {
                    return scaled;
                }
            }

            // fallback
            return Math.min(5, Math.max(1, raw));
        }
        public record Item(int rating, String content) {}
    }

    public record ReviewAnalyzeAiResponse(String mood, String insightSummary) {}
}

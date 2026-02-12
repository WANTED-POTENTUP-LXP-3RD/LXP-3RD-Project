package com.lxp.aplus.review.domain;

import com.lxp.aplus.common.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review_analysis")
public class ReviewsAnalysis extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long courseId;

    String mood;

    int day;

    @Column(length = 1000)
    String insightSummary;

    @Builder
    public ReviewsAnalysis(Long courseId, int day, String mood, String insightSummary) {
        this.courseId = courseId;
        this.day = day;
        this.mood = mood;
        this.insightSummary = insightSummary;
    }

    public static ReviewsAnalysis create(Long courseId, int day, String mood, String insightSummary) {

        return ReviewsAnalysis.builder()
                .courseId(courseId)
                .mood(mood)
                .day(day)
                .insightSummary(insightSummary)
                .build();
    }
}

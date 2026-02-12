package com.lxp.aplus.course.domain;

import com.lxp.aplus.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Table(name = "lecture_keywords")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureKeyword extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lecture_resource_id", nullable = false)
    private Long lectureResourceId;

    @Column(name = "keyword", nullable = false, length = 120)
    private String keyword;

    @Column(name = "importance", nullable = false, precision = 5, scale = 4)
    private BigDecimal importance;

    private LectureKeyword(Long lectureResourceId, String keyword, BigDecimal importance) {
        this.lectureResourceId = lectureResourceId;
        this.keyword = keyword;
        this.importance = importance;
    }

    public static LectureKeyword create(Long lectureResourceId, String keyword, BigDecimal importance) {
        return new LectureKeyword(lectureResourceId, keyword, importance);
    }
}

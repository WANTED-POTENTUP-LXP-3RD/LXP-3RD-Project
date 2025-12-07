package com.lxp.aplus.course.domain;

import com.lxp.aplus.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "lectures")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lecture extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LectureResource> lectureResources = new ArrayList<>();

    @Column (nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    public Lecture(Section section, String title, String description, int orderIndex) {
        this.section = section;
        this.title = title;
        this.description = description;
        this.orderIndex = orderIndex;
    }

    public static Lecture create(Section section, String title, String description, int orderIndex) {
        return new Lecture(section, title, description, orderIndex);
    }
}

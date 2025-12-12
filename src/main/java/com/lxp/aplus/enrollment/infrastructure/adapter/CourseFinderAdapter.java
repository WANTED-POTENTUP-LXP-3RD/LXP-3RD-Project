package com.lxp.aplus.enrollment.infrastructure.adapter;

import com.lxp.aplus.category.domain.Category;

import com.lxp.aplus.category.domain.CategoryRepository;

import com.lxp.aplus.course.domain.CourseRepository;

import com.lxp.aplus.enrollment.application.port.out.CourseFinder;

import com.lxp.aplus.enrollment.application.port.out.CourseSummary;

import com.lxp.aplus.enrollment.application.port.out.LectureResourceSummary;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;



import java.util.ArrayList;

import java.util.Collections;

import java.util.List;

import java.util.Optional;

import java.util.stream.Collectors;



@Component

@RequiredArgsConstructor

public class CourseFinderAdapter implements CourseFinder {



    private final CourseRepository courseRepository;

    private final CategoryRepository categoryRepository;



    @Override

    @Transactional(readOnly = true)

    public Optional<CourseSummary> findCourseById(Long courseId) {

        return courseRepository.findById(courseId)

                .map(course -> new CourseSummary(course.getId(), course.getTitle()));

    }



    @Override

    public int countLectures(Long courseId) {

        return courseRepository.countLecturesByCourseId(courseId);

    }



    @Override

    public List<Long> getLectureResourceIds(Long courseId) {

        return courseRepository.findAllLecturesWithResourcesByCourseId(courseId).stream()

                .flatMap(lecture -> lecture.getLectureResources().stream())

                .map(lectureResource -> lectureResource.getId())

                .collect(Collectors.toList());

    }



    @Override

    @Transactional(readOnly = true)

    public List<LectureResourceSummary> findAllLectureResourcesByCourseId(Long courseId) {

        return courseRepository.findAllLecturesWithResourcesByCourseId(courseId).stream()

                .flatMap(lecture -> lecture.getLectureResources().stream())

                .map(resource -> new LectureResourceSummary(

                        resource.getId(),

                        resource.getLecture().getTitle(),

                        resource.getLecture().getTotalDurationSeconds()

                ))

                .collect(Collectors.toList());

    }



        @Override



        @Transactional(readOnly = true)



        public List<String> findCategoryNamesByCourseId(Long courseId) {



            return courseRepository.findById(courseId)



                    .flatMap(course -> categoryRepository.findByIdWithParent(course.getCategoryId()))



                    .map(category -> {



                        List<String> categoryNames = new ArrayList<>();



                        Category currentCategory = category;



                        while (currentCategory != null) {



                            categoryNames.add(currentCategory.getName());



                            currentCategory = currentCategory.getParent();



                        }



                        Collections.reverse(categoryNames);



                        return categoryNames;



                    })



                    .orElse(Collections.emptyList());



        }

}

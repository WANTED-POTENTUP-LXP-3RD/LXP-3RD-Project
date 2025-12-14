package com.lxp.aplus.course.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.CourseErrorCode;
import com.lxp.aplus.course.application.command.CreateLectureCommand;
import com.lxp.aplus.course.application.command.UpdateLectureCommand;
import com.lxp.aplus.course.application.file.FileValidator;
import com.lxp.aplus.course.application.result.LectureResult;
import com.lxp.aplus.course.application.vo.UploadFile;
import com.lxp.aplus.course.domain.*;
import com.lxp.aplus.course.infrastructure.file.LectureFileStore;
import com.lxp.aplus.course.infrastructure.file.StoredFileInfo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class LectureCommandUseCase {

    private final CourseRepository courseRepository;
    private final FileValidator fileValidator;
    private final LectureFileStore lectureFileStore;

    public LectureResult createLecture(Long courseId, Long sectionId, CreateLectureCommand command) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));

        UploadFile file = command.resource().uploadFile();

        fileValidator.validateRequired(file);
        StoredFileInfo storedFile = lectureFileStore.storeLectureResourceFile(courseId, file);

        Lecture lecture = course.createLecture(
                sectionId,
                command.title(),
                command.totalDurationSeconds(),
                command.isPreview(),
                command.orderIndex(),
                command.resource().isDownloadable(),
                storedFile.fileKey(),
                storedFile.fileUrl(),
                file.originalFileName()
        );

        //Course saved = courseRepository.save(course);
        courseRepository.flush();

        return LectureResult.from(lecture);
    }

    public LectureResult updateLecture(Long courseId, Long lectureId, UpdateLectureCommand command) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));

        Lecture lecture = null;

        UploadFile file = command.resource().uploadFile();

        if (command.resource().uploadFile() == null) {
            fileValidator.validateIfPresent(file);

            lecture = course.updateLectureMeta(
                    lectureId,
                    command.title(),
                    command.totalDurationSeconds(),
                    command.isPreview(),
                    command.orderIndex()
            );
        } else {
            fileValidator.validateRequired(file);
            StoredFileInfo storedFile = lectureFileStore.storeLectureResourceFile(courseId, file);

            lecture = course.updateLectureWithResource(
                    lectureId,
                    command.title(),
                    command.totalDurationSeconds(),
                    command.isPreview(),
                    command.orderIndex(),
                    command.resource().isDownloadable(),
                    storedFile.fileKey(),
                    storedFile.fileUrl(),
                    file.originalFileName()
            );
        }

        courseRepository.save(course);
        return LectureResult.from(lecture);
    }

    public void deleteLecture(Long courseId, Long lectureId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));

        course.deleteLecture(lectureId);
    }
}

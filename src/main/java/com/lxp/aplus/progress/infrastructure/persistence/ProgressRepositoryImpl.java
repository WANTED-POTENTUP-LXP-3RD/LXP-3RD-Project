package com.lxp.aplus.progress.infrastructure.persistence;

import com.lxp.aplus.course.domain.LectureResource;
import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.progress.domain.Progress;
import com.lxp.aplus.progress.domain.ProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ProgressRepositoryImpl implements ProgressRepository {

    private final ProgressJpaRepository progressJpaRepository;

    @Override
    public Progress save(Progress progress) {
        return progressJpaRepository.save(progress);
    }

    @Override
    public Optional<Progress> findByEnrollmentAndLectureResource(Enrollment enrollment, LectureResource lectureResource) {
        return progressJpaRepository.findByEnrollmentAndLectureResource(enrollment, lectureResource);
    }

    @Override
    public List<Progress> findByEnrollmentId(Long enrollmentId) {
        return progressJpaRepository.findByEnrollmentId(enrollmentId);
    }

    @Override
    public long countByEnrollmentIdAndIsCompleted(Long enrollmentId, boolean isCompleted) {
        return progressJpaRepository.countByEnrollmentIdAndIsCompleted(enrollmentId, isCompleted);
    }

    @Override
    public List<Progress> findByEnrollmentIdAndLectureResourceIds(Long enrollmentId, List<Long> lectureResourceIds) {
        return progressJpaRepository.findByEnrollment_IdAndLectureResource_IdIn(enrollmentId, lectureResourceIds);
    }

    @Override
    public Map<Long, Boolean> getCompletionStatusMap(Long enrollmentId, List<Long> lectureResourceIds) {
        List<Progress> progresses = findByEnrollmentIdAndLectureResourceIds(enrollmentId, lectureResourceIds);
        return progresses.stream()
                .collect(Collectors.toMap(
                        progress -> progress.getLectureResource().getId(),
                        Progress::isCompleted
                ));
    }
}
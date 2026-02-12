package com.lxp.aplus.user.infrastructure.persistence;

import com.lxp.aplus.user.application.port.out.InstructorApplicationRepository;
import com.lxp.aplus.user.domain.InstructorApplication;
import com.lxp.aplus.user.domain.InstructorApplicationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InstructorApplicationRepositoryImpl implements InstructorApplicationRepository {
    private final InstructorApplicationJpaRepository jpaRepository;

    @Override
    public InstructorApplication save(InstructorApplication application) {
        return jpaRepository.save(application);
    }

    @Override
    public Optional<InstructorApplication> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<InstructorApplication> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId);
    }

    @Override
    public Page<InstructorApplication> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    public Page<InstructorApplication> findAllByStatus(InstructorApplicationStatus status, Pageable pageable) {
        return jpaRepository.findAllByStatus(status, pageable);
    }
}

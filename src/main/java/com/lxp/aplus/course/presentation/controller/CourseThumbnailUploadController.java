package com.lxp.aplus.course.presentation.controller;

import com.lxp.aplus.common.security.Authenticated;
import com.lxp.aplus.common.security.InstructorOnly;
import com.lxp.aplus.course.application.command.CourseThumbnailPresignCommand;
import com.lxp.aplus.course.application.result.CourseThumbnailPresignResult;
import com.lxp.aplus.course.application.usecase.CourseThumbnailPresignUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/instructor/resource")
public class CourseThumbnailUploadController {

    private final CourseThumbnailPresignUseCase courseThumbnailPresignUseCase;

    @InstructorOnly
    @PostMapping("/thumbnail")
    public ResponseEntity<CourseThumbnailPresignResult> presign(
            @Authenticated Long instructorId,
            @RequestBody @Valid CourseThumbnailPresignCommand command
    ) {
        return ResponseEntity.ok(courseThumbnailPresignUseCase.presign(instructorId, command));
    }
}

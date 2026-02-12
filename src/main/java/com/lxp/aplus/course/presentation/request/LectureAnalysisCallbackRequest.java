package com.lxp.aplus.course.presentation.request;

import com.lxp.aplus.course.application.command.SaveLectureAnalysisCallbackCommand;
import com.lxp.aplus.course.domain.AnalysisStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record LectureAnalysisCallbackRequest(
        @NotNull Long lectureResourceId,
        @NotBlank String requestId,
        @NotNull AnalysisStatus status,
        @Valid List<KeywordRequest> keywords,
        String errorMessage
) {
    public SaveLectureAnalysisCallbackCommand toCommand() {
        List<SaveLectureAnalysisCallbackCommand.KeywordCommand> keywordCommands =
                keywords == null ? null : keywords.stream()
                        .map(keyword -> new SaveLectureAnalysisCallbackCommand.KeywordCommand(
                                keyword.keyword(),
                                keyword.importance()
                        ))
                        .toList();

        return new SaveLectureAnalysisCallbackCommand(
                lectureResourceId,
                requestId,
                status,
                keywordCommands,
                errorMessage
        );
    }

    public record KeywordRequest(
            @NotBlank @Size(max = 120) String keyword,
            @NotNull @DecimalMin("0.0") @DecimalMax("1.0") BigDecimal importance
    ) {
    }
}

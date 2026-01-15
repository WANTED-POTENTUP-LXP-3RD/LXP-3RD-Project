package com.lxp.aplus.course.presentation.request;

import com.lxp.aplus.course.application.command.CourseCreateCommand;
import com.lxp.aplus.course.domain.CourseLevel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CourseCreateRequest(@NotBlank(message = "제목은 필수입니다.") String title,
                                  @NotNull(message = "카테고리는 필수입니다.") Long categoryId,
                                  @NotBlank(message = "한 줄 요약은 필수입니다.") String summary,
                                  @NotBlank(message = "상세 설명은 필수입니다.") String description,
                                  @NotNull(message = "가격은 필수입니다.") @Min(value = 0, message = "가격은 0원 이상이어야 합니다.") int price,
                                  @NotNull(message = "난이도는 필수입니다.") CourseLevel courseLevel
) {
    public CourseCreateCommand toCommand() {
        return CourseCreateCommand.builder()
                .title(this.title)
                .summary(this.summary)
                .description(this.description)
                .categoryId(this.categoryId)
                .courseLevel(this.courseLevel)
                .price(this.price)
                .thumbnailFile(null) // TODO: 썸네일 리소스 key 발급 로직 필요
                .build();
    }
}

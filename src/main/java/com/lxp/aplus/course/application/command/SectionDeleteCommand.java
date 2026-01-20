package com.lxp.aplus.course.application.command;

public record SectionDeleteCommand(
        Long courseId,
        Long instructorId,
        Long sectionId
) {
    public static SectionDeleteCommand of(Long courseId, Long instructorId, Long sectionId){
        return new SectionDeleteCommand(courseId, instructorId, sectionId);
    }
}

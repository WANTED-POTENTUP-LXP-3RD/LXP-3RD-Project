package com.lxp.aplus.course.application.command;

import com.lxp.aplus.course.application.vo.UploadFile;
import lombok.Builder;

@Builder
public record CreateLectureResourceCommand(
        boolean isDownloadable,
        UploadFile uploadFile
){
}

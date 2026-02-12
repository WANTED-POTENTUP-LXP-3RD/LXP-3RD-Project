package com.lxp.aplus.course.infrastructure.file;
import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.CourseErrorCode;
import com.lxp.aplus.common.error.code.GlobalErrorCode;
import org.springframework.stereotype.Component;

@Component
public class CourseThumbnailKeyGenerator {

    private static final String DIRECTORY_FORMAT =
            "courses/thumbnails/%d/%s/%s.%s"; // instructorId / yyyyMM / uuid / ext

    public String generate(Long instructorId, String originalFileName) {
        String ext = extractImageExt(originalFileName);
        String ym = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy/MM"));
        String uuid = java.util.UUID.randomUUID().toString().replace("-", "");
        return String.format(DIRECTORY_FORMAT, instructorId, ym, uuid, ext);
    }

    private String extractImageExt(String originalFileName) {
        int idx = originalFileName.lastIndexOf('.');
        if (idx < 0) throw new BusinessException(CourseErrorCode.COURSE_THUMBNAIL_EXTENSION_MISSING);
        String ext = originalFileName.substring(idx + 1).toLowerCase();
        return switch (ext) {
            case "jpg" -> "jpeg";
            case "jpeg", "png", "webp" -> ext;
            default -> throw new BusinessException(CourseErrorCode.COURSE_THUMBNAIL_EXTENSION_NOT_ALLOWED);
        };
    }
}

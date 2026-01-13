package com.lxp.aplus.course.domain;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.LectureResourceErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LectureResourceV2Test {
    @Test
    @DisplayName("비디오 파일일때 유효한 duration과 fileSize인 경우 정상 생성된다")
    void createVideoResource_withValidInputs_success() {
        // given
        String originalFileName = "lecture.mp4";
        String fileKey = "lecture/a5f76fee-ff9f-46c5-86d4-787d42950bc7";
        Integer videoDuration = 3600;
        boolean isDownloadable = true;
        long fileSize = 500000L;

        // when
        LectureResourceV2 resource = LectureResourceV2.create(
                originalFileName,
                fileKey,
                videoDuration,
                isDownloadable,
                fileSize
        );

        // then
        assertThat(resource.getOriginalFileName()).isEqualTo(originalFileName);
        assertThat(resource.getFileKey()).isEqualTo(fileKey);
        assertThat(resource.getResourceType()).isEqualTo(ResourceType.VIDEO);
        assertThat(resource.getVideoDuration().duration()).isEqualTo(videoDuration);
        assertThat(resource.isDownloadable()).isTrue();
    }

    @Test
    @DisplayName("PDF일때 유효한 fileSize인 경우 정상 생성된다")
    void createPdfResource_withoutDuration_success() {
        // given
        String originalFileName = "document.pdf";
        String fileKey = "lecture/a5f76fee-ff9f-46c5-86d4-787d42950bc7";
        long fileSize = 30000L;

        // when
        LectureResourceV2 resource = LectureResourceV2.create(
                originalFileName,
                fileKey,
                null,
                true,
                fileSize
        );

        // then
        assertThat(resource.getResourceType()).isEqualTo(ResourceType.PDF);
        assertThat(resource.getVideoDuration()).isNull();
    }

    @Test
    @DisplayName("DOC일때 유효한 fileSize인 경우 정상 생성된다")
    void createDocResource_success() {
        // given
        String originalFileName = "lecture.doc";
        String fileKey = "lecture/a5f76fee-ff9f-46c5-86d4-787d42950bc7";
        long fileSize = 30000L;

        // when
        LectureResourceV2 resource = LectureResourceV2.create(
                originalFileName,
                fileKey,
                null,
                true,
                fileSize
        );

        // then
        assertThat(resource.getResourceType()).isEqualTo(ResourceType.DOC);
    }

    @Test
    @DisplayName("ZIP일때 유효한 fileSize인 경우 정상 생성된다")
    void createZipResource_success() {
        // given
        String originalFileName = "materials.zip";
        String fileKey = "lecture/a5f76fee-ff9f-46c5-86d4-787d42950bc7";
        long fileSize = 500000L;

        // when
        LectureResourceV2 resource = LectureResourceV2.create(
                originalFileName,
                fileKey,
                null,
                true,
                fileSize
        );

        // then
        assertThat(resource.getResourceType()).isEqualTo(ResourceType.ZIP);
    }

    @ParameterizedTest
    @CsvSource({
            "video.mp4, VIDEO, 1000000",
            "file.zip, ZIP, 1000000",
            "doc.pdf, PDF, 50000",
            "file.doc, DOC, 50000"
    })
    @DisplayName("각 리소스 타입별 최대 파일 크기 경계값에서 정상 생성된다")
    void createResource_withMaxFileSize_success(String fileName, ResourceType expectedType, long maxSize) {
        // given
        Integer duration = expectedType == ResourceType.VIDEO ? 3600 : null;

        // when
        LectureResourceV2 resource = LectureResourceV2.create(
                fileName,
                "key/" + fileName,
                duration,
                true,
                maxSize
        );

        // then
        assertThat(resource.getResourceType()).isEqualTo(expectedType);
    }

    @Test
    @DisplayName("비디오 파일인데 duration이 null이면 예외가 발생한다")
    void createVideoResource_withNullDuration_throwsException() {
        // given
        String originalFileName = "lecture.mp4";
        String fileKey = "lecture/a5f76fee-ff9f-46c5-86d4-787d42950bc7";
        long fileSize = 500000L;

        // when & then
        assertThatThrownBy(() -> LectureResourceV2.create(
                originalFileName,
                fileKey,
                null,
                true,
                fileSize
        ))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode",
                        LectureResourceErrorCode.LECTURE_RESOURCE_VIDEO_DURATION_NOT_FOUND);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -100})
    @DisplayName("비디오 파일인데 duration이 0 이하이면 예외가 발생한다")
    void createVideoResource_withInvalidDuration_throwsException(int invalidDuration) {
        // given
        String originalFileName = "lecture.mp4";
        String fileKey = "lecture/a5f76fee-ff9f-46c5-86d4-787d42950bc7";
        long fileSize = 500000L;

        // when & then
        assertThatThrownBy(() -> LectureResourceV2.create(
                originalFileName,
                fileKey,
                invalidDuration,
                true,
                fileSize
        ))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode",
                        LectureResourceErrorCode.LECTURE_RESOURCE_VIDEO_DURATION_NOT_FOUND);
    }

    @Test
    @DisplayName("비디오 파일이 최대 크기를 초과하면 예외가 발생한다")
    void createVideoResource_exceedsMaxSize_throwsException() {
        // given
        String originalFileName = "lecture.mp4";
        String fileKey = "lecture/a5f76fee-ff9f-46c5-86d4-787d42950bc7";
        Integer videoDuration = 3600;
        long fileSize = 1000001L; // 최대 1000000L 초과

        // when & then
        assertThatThrownBy(() -> LectureResourceV2.create(
                originalFileName,
                fileKey,
                videoDuration,
                true,
                fileSize
        ))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode",
                        LectureResourceErrorCode.FILE_SIZE_EXCEEDED);
    }

    @Test
    @DisplayName("ZIP 파일이 최대 크기를 초과하면 예외가 발생한다")
    void createZipResource_exceedsMaxSize_throwsException() {
        // given
        String originalFileName = "materials.zip";
        String fileKey = "lecture/a5f76fee-ff9f-46c5-86d4-787d42950bc7";
        long fileSize = 1000001L;

        // when & then
        assertThatThrownBy(() -> LectureResourceV2.create(
                originalFileName,
                fileKey,
                null,
                true,
                fileSize
        ))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode",
                        LectureResourceErrorCode.FILE_SIZE_EXCEEDED);
    }

    @Test
    @DisplayName("PDF 파일이 최대 크기를 초과하면 예외가 발생한다")
    void createPdfResource_exceedsMaxSize_throwsException() {
        // given
        String originalFileName = "document.pdf";
        String fileKey = "lecture/a5f76fee-ff9f-46c5-86d4-787d42950bc7";
        long fileSize = 50001L; // 최대 50000L 초과

        // when & then
        assertThatThrownBy(() -> LectureResourceV2.create(
                originalFileName,
                fileKey,
                null,
                true,
                fileSize
        ))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode",
                        LectureResourceErrorCode.FILE_SIZE_EXCEEDED);
    }

    @Test
    @DisplayName("DOC 파일이 최대 크기를 초과하면 예외가 발생한다")
    void createDocResource_exceedsMaxSize_throwsException() {
        // given
        String originalFileName = "lecture.doc";
        String fileKey = "lecture/a5f76fee-ff9f-46c5-86d4-787d42950bc7";
        long fileSize = 50001L;

        // when & then
        assertThatThrownBy(() -> LectureResourceV2.create(
                originalFileName,
                fileKey,
                null,
                true,
                fileSize
        ))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode",
                        LectureResourceErrorCode.FILE_SIZE_EXCEEDED);
    }

    @ParameterizedTest
    @CsvSource({
            "video.mp4, 3600, 1000001",
            "file.zip, , 1000001",
            "doc.pdf, , 50001",
            "file.doc, , 50001"
    })
    @DisplayName("각 리소스 타입별 최대 크기 초과 시 예외가 발생한다")
    void createResource_exceedsMaxSize_throwsException(
            String fileName,
            Integer duration,
            long fileSize
    ) {
        // given & when & then
        assertThatThrownBy(() -> LectureResourceV2.create(
                fileName,
                "key/" + fileName,
                duration,
                true,
                fileSize
        ))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode",
                        LectureResourceErrorCode.FILE_SIZE_EXCEEDED);
    }

    @ParameterizedTest
    @ValueSource(strings = {"lecture.mp4", "document.pdf", "file.doc", "archive.zip"})
    @DisplayName("유효한 확장자면 정상적으로 생성된다")
    void createWithValidExtension(String fileName) {
        // given
        Lecture lecture = createMockLecture();

        // when
        LectureResource resource = LectureResource.create(
                lecture, true, "fileKey", "fileUrl", fileName
        );

        // then
        assertThat(resource.getOriginalFileName()).isEqualTo(fileName);
    }

    @ParameterizedTest
    @ValueSource(strings = {"image.jpg", "image.png", "script.js", "style.css", "data.txt", "app.exe"})
    @DisplayName("지원하지 않는 확장자면 예외가 발생한다")
    void throwExceptionForUnsupportedExtension(String fileName) {
        // given
        Lecture lecture = createMockLecture();

        // when & then
        assertThatThrownBy(() ->
                LectureResource.create(lecture, true, "fileKey", "fileUrl", fileName)
        )
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", LectureResourceErrorCode.LECTURE_RESOURCE_UNSUPPORTED_EXTENSION);
    }

    @ParameterizedTest
    @ValueSource(strings = {"noextension", "file.", ".hiddenfile"})
    @DisplayName("확장자가 없거나 잘못된 형식이면 예외가 발생한다")
    void throwExceptionForMissingExtension(String fileName) {
        // given
        Lecture lecture = createMockLecture();

        // when & then
        assertThatThrownBy(() ->
                LectureResource.create(lecture, true, "fileKey", "fileUrl", fileName)
        )
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", LectureResourceErrorCode.LECTURE_RESOURCE_UNSUPPORTED_EXTENSION);
    }

    private Lecture createMockLecture() {
        return Lecture.builder().build();
    }
}
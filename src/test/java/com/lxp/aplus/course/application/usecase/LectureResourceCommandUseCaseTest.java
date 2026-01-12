package com.lxp.aplus.course.application.usecase;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.course.application.command.CreatePresignedUrlCommand;
import com.lxp.aplus.course.application.port.out.PresignedUrlGenerator;
import com.lxp.aplus.course.application.result.PresignedUrlResult;
import com.lxp.aplus.course.domain.LectureResourceRepository;
import com.lxp.aplus.course.domain.LectureResourceV2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class LectureResourceCommandUseCaseTest {
    @Mock
    private PresignedUrlGenerator presignedUrlGenerator;

    @Mock
    private LectureResourceRepository lectureResourceRepository;

    @InjectMocks
    private LectureResourceCommandUseCase useCase;

    @Test
    @DisplayName("정상적으로 presigned URL을 생성하고 리소스를 저장한다")
    void generatePresignedUrl_success() {
        // given
        CreatePresignedUrlCommand command = new CreatePresignedUrlCommand(
                "lecture.mp4",
                "video/mp4",
                3600L,
                600,
                false
        );

        PresignedUrlResult expectedResult = new PresignedUrlResult(
                "https://s3.amazonaws.com/bucket/presigned-url",
                "lecture/cde98e11-abb9-4a4b-907d-4a2a5e1ab00c.mp4",
                "PUT",
                120
        );

        given(presignedUrlGenerator.generatePutUrl(command.fileName(), command.contentType()))
                .willReturn(expectedResult);

        // when
        PresignedUrlResult result = useCase.generatePresignedUrl(command);

        // then
        assertThat(result).isEqualTo(expectedResult);
        then(lectureResourceRepository).should().save(any(LectureResourceV2.class));
    }


    @Test
    @DisplayName("Entity 생성 실패 시 저장되지 않는다")
    void generatePresignedUrl_entityCreationFails_notSaved() {
        // given
        CreatePresignedUrlCommand command = new CreatePresignedUrlCommand(
                "lecture.mp4",
                "video/mp4",
                3600L,
                null,
                false
        );

        PresignedUrlResult presignedUrlResult = new PresignedUrlResult(
                "https://s3.amazonaws.com/bucket/presigned-url",
                "lecture/cde98e11-abb9-4a4b-907d-4a2a5e1ab00c.mp4",
                "PUT",
                120
        );

        given(presignedUrlGenerator.generatePutUrl(anyString(), anyString()))
                .willReturn(presignedUrlResult);

        // when & then
        assertThatThrownBy(() -> useCase.generatePresignedUrl(command))
                .isInstanceOf(BusinessException.class);

        then(lectureResourceRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("정상적으로 GET presigned URL을 반환한다")
    void generateGetUrl_success() {
        // given
        String key = "videos/2024/lecture.mp4";;

        PresignedUrlResult expectedResult = new PresignedUrlResult(
                "https://s3.amazonaws.com/bucket/presigned-url",
                "lecture/cde98e11-abb9-4a4b-907d-4a2a5e1ab00c.mp4",
                "PUT",
                120
        );

        given(presignedUrlGenerator.generateGetUrl(key))
                .willReturn(expectedResult);

        // when
        PresignedUrlResult result = useCase.generateGetUrl(key);

        // then
        assertThat(result).isEqualTo(expectedResult);
    }
}
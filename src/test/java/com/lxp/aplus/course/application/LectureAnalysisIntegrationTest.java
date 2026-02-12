package com.lxp.aplus.course.application;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.LectureResourceErrorCode;
import com.lxp.aplus.course.application.port.out.LectureAnalysisPort;
import com.lxp.aplus.course.application.usecase.LectureAnalysisCommandUseCase;
import com.lxp.aplus.course.domain.AnalysisStatus;
import com.lxp.aplus.course.domain.LectureResourceRepository;
import com.lxp.aplus.course.domain.LectureResourceV2;
import com.lxp.aplus.course.infrastructure.persistence.LectureKeywordJpaRepository;
import com.lxp.aplus.course.presentation.request.LectureAnalysisCallbackRequest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("강의 분석 통합 테스트")
class LectureAnalysisIntegrationTest {

    @Autowired
    private LectureAnalysisCommandUseCase lectureAnalysisCommandUseCase;

    @Autowired
    private LectureResourceRepository lectureResourceRepository;

    @Autowired
    private LectureKeywordJpaRepository lectureKeywordJpaRepository;

    @Autowired
    private EntityManager entityManager;

    @Value("${analysis.callback-secret}")
    private String callbackSecret;

    @MockBean
    private LectureAnalysisPort lectureAnalysisPort;

    @Test
    @DisplayName("성공: 콜백 성공 시 키워드가 저장되고 상태가 SUCCESS로 변경된다")
    void callback_success_saves_keywords_and_updates_status() {
        LectureResourceV2 resource = createLectureResource();

        LectureAnalysisCallbackRequest request = new LectureAnalysisCallbackRequest(
                resource.getId(),
                "req-123",
                AnalysisStatus.SUCCESS,
                List.of(
                        new LectureAnalysisCallbackRequest.KeywordRequest("자바", new BigDecimal("0.95")),
                        new LectureAnalysisCallbackRequest.KeywordRequest("스트림", new BigDecimal("0.89"))
                ),
                null
        );

        lectureAnalysisCommandUseCase.handleCallback(callbackSecret, request.toCommand());
        flushAndClear();

        LectureResourceV2 updated = lectureResourceRepository.findById(resource.getId()).orElseThrow();
        assertThat(updated.getAnalysisStatus()).isEqualTo(AnalysisStatus.SUCCESS);

        var keywords = lectureKeywordJpaRepository.findByLectureResourceId(resource.getId());
        assertThat(keywords).hasSize(2);
        assertThat(keywords.get(0).getKeyword()).isNotBlank();
    }

    @Test
    @DisplayName("성공: 콜백 실패 시 상태가 FAILED로 변경되고 키워드는 저장되지 않는다")
    void callback_failed_updates_status_only() {
        LectureResourceV2 resource = createLectureResource();

        LectureAnalysisCallbackRequest request = new LectureAnalysisCallbackRequest(
                resource.getId(),
                "req-456",
                AnalysisStatus.FAILED,
                null,
                "whisper timeout"
        );

        lectureAnalysisCommandUseCase.handleCallback(callbackSecret, request.toCommand());
        flushAndClear();

        LectureResourceV2 updated = lectureResourceRepository.findById(resource.getId()).orElseThrow();
        assertThat(updated.getAnalysisStatus()).isEqualTo(AnalysisStatus.FAILED);
        assertThat(lectureKeywordJpaRepository.findByLectureResourceId(resource.getId())).isEmpty();
    }

    @Test
    @DisplayName("성공: 분석 시작 시 상태가 PROCESSING으로 변경되고 Python 호출이 발생한다")
    void start_analysis_updates_status_and_calls_python() {
        LectureResourceV2 resource = createLectureResource();

        lectureAnalysisCommandUseCase.startAnalysis(resource.getId());
        flushAndClear();

        LectureResourceV2 updated = lectureResourceRepository.findById(resource.getId()).orElseThrow();
        assertThat(updated.getAnalysisStatus()).isEqualTo(AnalysisStatus.PROCESSING);

        verify(lectureAnalysisPort).startAnalysisAsync(eq(resource.getId()), contains(resource.getFileKey()), anyString());
    }

    @Test
    @DisplayName("성공: 이미 PROCESSING 상태면 분석 시작 재호출이 차단된다")
    void start_analysis_rejected_when_already_processing() {
        LectureResourceV2 resource = createLectureResource();
        lectureAnalysisCommandUseCase.startAnalysis(resource.getId());

        assertThatThrownBy(() -> lectureAnalysisCommandUseCase.startAnalysis(resource.getId()))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(LectureResourceErrorCode.LECTURE_RESOURCE_ANALYSIS_ALREADY_PROCESSING);
    }

    @Test
    @DisplayName("성공: Python 호출 실패 시 상태가 FAILED로 변경된다")
    void start_analysis_failed_when_python_request_fails() {
        LectureResourceV2 resource = createLectureResource();
        doThrow(new RuntimeException("python down"))
                .when(lectureAnalysisPort)
                .startAnalysisAsync(eq(resource.getId()), contains(resource.getFileKey()), anyString());

        assertThatThrownBy(() -> lectureAnalysisCommandUseCase.startAnalysis(resource.getId()))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(LectureResourceErrorCode.STORAGE_CLIENT_INTERNAL_ERROR);

        flushAndClear();
        LectureResourceV2 updated = lectureResourceRepository.findById(resource.getId()).orElseThrow();
        assertThat(updated.getAnalysisStatus()).isEqualTo(AnalysisStatus.FAILED);
    }

    @Test
    @DisplayName("성공: 중복 callback은 무해 처리된다")
    void callback_duplicate_is_ignored() {
        LectureResourceV2 resource = createLectureResource();

        LectureAnalysisCallbackRequest first = new LectureAnalysisCallbackRequest(
                resource.getId(),
                "req-dup-1",
                AnalysisStatus.SUCCESS,
                List.of(new LectureAnalysisCallbackRequest.KeywordRequest("자바", new BigDecimal("0.95"))),
                null
        );

        LectureAnalysisCallbackRequest duplicate = new LectureAnalysisCallbackRequest(
                resource.getId(),
                "req-dup-1",
                AnalysisStatus.SUCCESS,
                List.of(new LectureAnalysisCallbackRequest.KeywordRequest("파이썬", new BigDecimal("0.99"))),
                null
        );

        lectureAnalysisCommandUseCase.handleCallback(callbackSecret, first.toCommand());
        lectureAnalysisCommandUseCase.handleCallback(callbackSecret, duplicate.toCommand());
        flushAndClear();

        var keywords = lectureKeywordJpaRepository.findByLectureResourceId(resource.getId());
        assertThat(keywords).hasSize(1);
        assertThat(keywords.get(0).getKeyword()).isEqualTo("자바");
    }

    @Test
    @DisplayName("성공: FAILED 이후 SUCCESS 콜백이 오면 복구 반영된다")
    void callback_success_after_failed_is_applied() {
        LectureResourceV2 resource = createLectureResource();

        LectureAnalysisCallbackRequest failed = new LectureAnalysisCallbackRequest(
                resource.getId(),
                "req-recover-1",
                AnalysisStatus.FAILED,
                null,
                "temporary timeout"
        );

        LectureAnalysisCallbackRequest success = new LectureAnalysisCallbackRequest(
                resource.getId(),
                "req-recover-1",
                AnalysisStatus.SUCCESS,
                List.of(new LectureAnalysisCallbackRequest.KeywordRequest("복구", new BigDecimal("0.91"))),
                null
        );

        lectureAnalysisCommandUseCase.handleCallback(callbackSecret, failed.toCommand());
        lectureAnalysisCommandUseCase.handleCallback(callbackSecret, success.toCommand());
        flushAndClear();

        LectureResourceV2 updated = lectureResourceRepository.findById(resource.getId()).orElseThrow();
        assertThat(updated.getAnalysisStatus()).isEqualTo(AnalysisStatus.SUCCESS);
        assertThat(lectureKeywordJpaRepository.findByLectureResourceId(resource.getId())).hasSize(1);
    }

    private LectureResourceV2 createLectureResource() {
        LectureResourceV2 resource = LectureResourceV2.create(
                "sample.mp4",
                "resources/lectureResource/1/sample.mp4",
                120,
                false,
                1000L
        );
        lectureResourceRepository.save(resource);
        flushAndClear();
        return lectureResourceRepository.findById(resource.getId()).orElseThrow();
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}

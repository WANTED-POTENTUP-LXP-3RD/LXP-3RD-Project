package com.lxp.aplus.course.application;

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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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

        verify(lectureAnalysisPort).startAnalysisAsync(eq(resource.getId()), eq(resource.getFileKey()), anyString());
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

package com.lxp.aplus.course.infrastructure.adapter;

import com.lxp.aplus.course.application.port.out.LectureAnalysisPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class LectureAnalysisAdapter implements LectureAnalysisPort {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${analysis.python-base-url}")
    private String pythonBaseUrl;

    @Value("${analysis.callback-url}")
    private String callbackUrl;

    @Override
    public void startAnalysisAsync(Long lectureResourceId, String videoUrl, String requestId) {
        String url = buildStartUrl();
        AnalysisStartRequest request = new AnalysisStartRequest(
                lectureResourceId,
                videoUrl,
                callbackUrl,
                requestId
        );
        log.info("Requesting python analysis. lectureResourceId={}, requestId={}, callbackUrl={}", lectureResourceId, requestId, callbackUrl);
        restTemplate.postForEntity(url, request, Void.class);
    }

    private String buildStartUrl() {
        if (pythonBaseUrl.endsWith("/")) {
            return pythonBaseUrl.substring(0, pythonBaseUrl.length() - 1) + "/internal/analysis/start";
        }
        return pythonBaseUrl + "/internal/analysis/start";
    }

    private record AnalysisStartRequest(
            Long lectureResourceId,
            String videoUrl,
            String callbackUrl,
            String requestId
    ) {
    }
}

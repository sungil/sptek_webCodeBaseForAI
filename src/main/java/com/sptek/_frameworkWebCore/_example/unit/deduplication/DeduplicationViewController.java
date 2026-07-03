package com.sptek._frameworkWebCore._example.unit.deduplication;

import com.sptek._frameworkWebCore._annotation.Enable_ResponseOfViewGlobalException_At_ViewController;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 중복 요청 방지 API를 브라우저에서 반복 호출해 볼 수 있는 예제 화면 컨트롤러.
 *
 * <p>Swagger UI로는 브라우저 캐시, timestamp query, GET/POST 연속 클릭 차이를 확인하기 어렵기 때문에
 * 전용 Thymeleaf 페이지로 라우팅하는 역할만 담당한다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@Controller
@Enable_ResponseOfViewGlobalException_At_ViewController
@RequestMapping(value = "/view/example/", produces = MediaType.TEXT_HTML_VALUE)

public class DeduplicationViewController {

    @NonFinal
    private final String htmlBasePath = "pages/_example/unit/";

    /**
     * 중복 요청 방지 API 테스트용 Thymeleaf 페이지를 반환한다.
     */
    @GetMapping("/deduplication/preventDuplicateRequest")
    public String preventDuplicateRequest() {
        return htmlBasePath + "preventDuplicationRequest";
    }
}

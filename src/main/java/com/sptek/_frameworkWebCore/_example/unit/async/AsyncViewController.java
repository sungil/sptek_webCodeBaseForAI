package com.sptek._frameworkWebCore._example.unit.async;

import com.sptek._frameworkWebCore._annotation.Enable_ResponseOfViewGlobalException_At_ViewController;
import com.sptek._frameworkWebCore.springSecurity.extras.dto.SignupRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * async 예제 API를 화면에서 호출하거나 지연 응답 동작을 확인하는 View 예제 컨트롤러.
 *
 * <p>Thymeleaf 화면에서 {@code requestFetch} 같은 프론트엔드 호출 유틸로 async API를 연속 호출하거나,
 * 긴 API 처리 중 화면의 대기/응답 순서를 확인하는 예제를 추가할 때 이 구성을 참고한다.
 * View 요청은 API 공통 응답 래핑이 아니라 {@code Enable_ResponseOfViewGlobalException_At_ViewController} 기반의
 * 화면 예외 흐름을 사용한다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@Controller
@Enable_ResponseOfViewGlobalException_At_ViewController
@RequestMapping(value = "/view/example/", produces = MediaType.TEXT_HTML_VALUE)
public class AsyncViewController {

    @NonFinal
    private final String htmlBasePath = "pages/_example/unit/";
    private final AsyncService asyncService;


    /**
     * async 예제 화면을 반환하는 placeholder 엔드포인트.
     *
     * <p>실제 화면 예제를 확장할 때는 {@code htmlBasePath} 아래 템플릿을 두고,
     * API 호출은 {@link AsyncApiController}의 async 비교 API를 사용해 응답 순서와 worker 반환 차이를 확인한다.</p>
     */
    @GetMapping("/async/xx")
    public String xx(Model model , SignupRequestDto signupRequestDto) {
        return htmlBasePath + "xx";
    }

}

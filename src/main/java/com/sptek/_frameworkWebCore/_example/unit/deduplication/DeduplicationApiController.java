package com.sptek._frameworkWebCore._example.unit.deduplication;

import com.sptek._frameworkWebCore._annotation.Enable_PreventDuplicateRequest_At_RestController_RestControllerMethod;
import com.sptek._frameworkWebCore._annotation.Enable_ResponseOfApiCommonSuccess_At_RestController;
import com.sptek._frameworkWebCore._annotation.Enable_ResponseOfApiGlobalException_At_RestController;
import com.sptek._frameworkWebCore.util.Timer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 프레임워크 중복 요청 방지 인터셉터의 RestController 적용 방식을 확인하는 예제 API 컨트롤러.
 *
 * <p>{@code Enable_PreventDuplicateRequest_At_RestController_RestControllerMethod}가 붙은 API를
 * 짧은 시간 안에 반복 호출했을 때 동일 세션 기준으로 후속 요청이 차단되는지 확인한다.
 * Swagger UI는 연속 요청 상황을 만들기 어렵기 때문에 전용 예제 페이지에서 테스트한다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@Enable_ResponseOfApiCommonSuccess_At_RestController
@Enable_ResponseOfApiGlobalException_At_RestController
@RequestMapping(value = {"/api/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
@Tag(name = "Prevent Duplication Request (스웨거에서 테스트 불가, Example 페이지에서 테스트 할것)", description = "")

public class DeduplicationApiController {

    /**
     * 처리 시간을 의도적으로 지연시켜 동일 세션의 빠른 연속 호출 차단 여부를 확인한다.
     */
    @Enable_PreventDuplicateRequest_At_RestController_RestControllerMethod
    @RequestMapping(value = "/01/example/deduplication/preventDuplicateRequest", method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "01. 동일 요청이 빠르게 연속 요청 되는 것을 방지", description = "")
    public Object duplicatedRequest() {
        Timer.sleep(3000L);
        return "prevent duplicateRequest test ok";
    }
}



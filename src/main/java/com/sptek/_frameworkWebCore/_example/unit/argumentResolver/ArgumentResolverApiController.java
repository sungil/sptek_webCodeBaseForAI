package com.sptek._frameworkWebCore._example.unit.argumentResolver;

import com.sptek._frameworkWebCore._example.dto.ExUserDto;
import com.sptek._frameworkWebCore._annotation.Enable_ArgumentResolver_At_Param;
import com.sptek._frameworkWebCore._annotation.Enable_ResponseOfApiCommonSuccess_At_RestController;
import com.sptek._frameworkWebCore._annotation.Enable_ResponseOfApiGlobalException_At_RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@Enable_ResponseOfApiCommonSuccess_At_RestController
@Enable_ResponseOfApiGlobalException_At_RestController
@RequestMapping(value = {"/api/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
@Tag(name = "Argument Resolver", description = "")
/**
 * Spring MVC 기본 바인딩과 커스텀 {@code HandlerMethodArgumentResolver} 적용 방식을 비교하는 예제 API.
 *
 * <p>단순 요청 파라미터 바인딩으로 충분한 DTO는 {@code @ModelAttribute} 같은 기본 MVC 바인딩을 사용한다.
 * 요청 정보, 세션, 인증 정보, 여러 파라미터 조합처럼 별도 조립 규칙이 필요한 경우에는
 * 파라미터에 {@code Enable_ArgumentResolver_At_Param}을 붙여 프로젝트 공통 또는 업무용 ArgumentResolver가
 * 명시적으로 표시된 파라미터만 처리하게 한다.</p>
 */
public class ArgumentResolverApiController {

    /**
     * 커스텀 ArgumentResolver 없이 Spring MVC 기본 모델 바인딩만 사용하는 예시.
     */
    @PostMapping(value = "/01/example/argumentResolver/withoutArgumentResolver", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    @Operation(summary = "01. ArgumentResolver 비적용", description = "")
    public Object withoutArgumentResolver(@ModelAttribute ExUserDto exUserDto) {
        return exUserDto;
    }

    /**
     * DTO 파라미터에 마커 애노테이션을 붙여 커스텀 ArgumentResolver 적용 대상을 명시하는 예시.
     */
    @PostMapping(value = "/02/example/argumentResolver/withArgumentResolver", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    @Operation(summary = "02. ArgumentResolver 적용", description = "")
    public Object withArgumentResolver(@Enable_ArgumentResolver_At_Param ExUserDto exUserDto) {
        return exUserDto;
    }
}

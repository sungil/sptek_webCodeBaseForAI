package com._sptek._webFrameworkExample.unit.responseError;

import com._sptek._webFrameworkExample.dto.ValidatedDto;
import com._sptek.__webFramework.api.response.Enable_ResponseOfApiCommonSuccess_At_RestController;
import com._sptek.__webFramework.api.response.Enable_ResponseOfApiGlobalException_At_RestController;
import com._sptek.__webFramework.core.exception.ServiceException;
import com._sptek._webFrameworkExample.common.resultCode.ServiceErrorCodeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@Enable_ResponseOfApiCommonSuccess_At_RestController
@Enable_ResponseOfApiGlobalException_At_RestController
@RequestMapping(value = {"/api/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
@Tag(name = "Response Error", description = "")

public class ResponseErrorApiController {
    private final ResponseErrorService responseErrorService;

    @PostMapping("/01/example/responseError/dtoValidation")
    @Operation(summary = "01. 객체의 validation 에러", description = "")
    public Object dtoValidation(@RequestBody ValidatedDto validatedDto) {
        int userIdSize = validatedDto.getUserId().length(); //NPE 발생 시키기 위해 임의 작성
        return validatedDto;
    }

    @GetMapping("/02/example/responseError/runtimeError")
    @Operation(summary = "02. Runtime 에러 ", description = "")
    public Object runtimeError(@Parameter(hidden = true) @RequestHeader("X-Auth") String authHeader) {
        return authHeader;
    }


    @GetMapping("/03/example/responseError/authError")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "03. 권한 에러", description = "")
    public Object authError() {
        return "authError";
    }

    @GetMapping("/04/example/responseError/serviceErrorWithDefaultMessage")
    @Operation(summary = "04. 서비스 에러 처리 (기본 메시지)", description = "")
    public Object serviceErrorWithDefaultMessage() {
        if(true) throw new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR);
        return "serviceErrorWithDefaultMessage";
    }

    @GetMapping("/05/example/responseError/serviceErrorWithUserMessage")
    @Operation(summary = "05. 서비스 에러 처리 (유저 메세지)", description = "")
    public Object serviceErrorWithUserMessage() {
        if (true) {
            throw new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, "주문 내역이 없습니다.");
        }
        return "serviceErrorWithUserMessage";
    }

    @GetMapping("/06/example/responseError/badServiceErrorUsage")
    @Operation(summary = "06. 서비스 에러 처리의 Bad 예시 (ID 중복 검사 가정)", description = "")
    public Object badServiceErrorUsage(@RequestParam String userId) {
        return responseErrorService.isAvailableId_badExample(userId);
    }

    @GetMapping("/07/example/responseError/goodServiceErrorUsage")
    @Operation(summary = "07. 서비스 에러 처리의 Good 예시 (ID 중복 검사 가정)", description = "")
    public Object goodServiceErrorUsage(@RequestParam String userId) {
        return responseErrorService.isAvailableId_goodExample(userId);
    }

}

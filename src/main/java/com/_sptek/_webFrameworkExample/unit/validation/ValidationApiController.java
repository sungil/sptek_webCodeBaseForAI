package com._sptek._webFrameworkExample.unit.validation;

import com._sptek._webFrameworkExample.dto.ValidatedDto;
import com._sptek.__webFramework.api.response.Enable_ResponseOfApiCommonSuccess_At_RestController;
import com._sptek.__webFramework.api.response.Enable_ResponseOfApiGlobalException_At_RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@Enable_ResponseOfApiCommonSuccess_At_RestController
@Enable_ResponseOfApiGlobalException_At_RestController
@RequestMapping(value = {"/api/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
@Tag(name = "Validation", description = "")

public class ValidationApiController {

    @GetMapping("/01/example/validation/validationAnnotation")
    @Operation(summary = "01. parameter 의 validation 확인과 에러 처리", description = "")
    public Object validationAnnotationGet(@Validated ValidatedDto validationTestDto) {
        return validationTestDto;
    }

    @PostMapping("/02/example/validation/validationAnnotation")
    @Operation(summary = "02. body 의 validation 확인과 에러 처리", description = "")
    public Object validationAnnotation(@RequestBody @Validated ValidatedDto validationTestDto) {
        return validationTestDto;
    }

    @PostMapping("/03/example/validation/validationAnnotationIgnore")
    @Operation(summary = "03. body 의 validation 미적용 처리", description = "")
    public Object validationAnnotationIgnore(@RequestBody ValidatedDto validationTestDto) {
        return validationTestDto;
    }
}

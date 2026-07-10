package com._sptek._webFrameworkExample.unit.validation;

import com._sptek._webFrameworkExample.dto.ValidatedDto;
import com._sptek.__webFramework.view.error.Enable_ResponseOfViewGlobalException_At_ViewController;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
@Enable_ResponseOfViewGlobalException_At_ViewController
@RequestMapping(value = "/view/example/", produces = MediaType.TEXT_HTML_VALUE)

public class ValidationViewController {

    @NonFinal
    private final String htmlBasePath = "pages/_example/unit/";

    //thymeleaf 에서 폼 입력 데이터 에 대한 validation 에러 처리 방법
    @GetMapping("/validation/withBindingResultForm")
    public String withBindingResultForm(ValidatedDto validatedDto) { //파람에 선언시 자동 으로 Model 에 포함됨 (설정된 디폴트 값 적용됨)
        return htmlBasePath + "validationWithBindingResult";
    }

    @PostMapping("/validation/withBindingResult")
    public String validationWithBindingResult(@Valid ValidatedDto validatedDto, BindingResult bindingResult) {
        // Dto 에 설정된 validation 요건에 맞지 않는 경우
        if (bindingResult.hasErrors()) {
            return htmlBasePath + "validationWithBindingResult";
        }

        // 별도의 validation 을 추가로 적용할 경우
        if (StringUtils.hasText(validatedDto.getEmail()) && validatedDto.getEmail().contains("@naver.com")) {
            bindingResult.rejectValue("email", "emailFail", "네이버 메일은 사용할 수 없습니다.");
            return htmlBasePath + "validationWithBindingResult";
        }

        //do what you want.
        return "redirect:" + "validationWithBindingResult";
    }
}

package com._sptek._webFrameworkExample.unit.deprecated;

import com._sptek.__webFramework.view.error.Enable_ResponseOfViewGlobalException_At_ViewController;
import com._sptek.__webFramework.core.modelMapper.ModelMapperUtil;
import com._sptek._webFrameworkExample.dto.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
@RequiredArgsConstructor
@Enable_ResponseOfViewGlobalException_At_ViewController
@RequestMapping(value = "/view/example/", produces = MediaType.TEXT_HTML_VALUE)

public class DEPRECATED_ViewController {
    @NonFinal
    private final String htmlBasePath = "pages/_example/unit/";

    //todo : Document에 대한 cache 처리를 위한건데.. 현재 cache 가 동작하지 않음(이유 확인이 필요함)
    @RequestMapping("/httpCache")
    public String httpCache(HttpServletResponse response, Model model) {
        long result = System.currentTimeMillis();

        // CacheControl을 이용한 캐시 헤더 설정
        CacheControl cacheControl = CacheControl.maxAge(60L, TimeUnit.SECONDS)  // 60초 동안 캐시
                .cachePublic()  // 공용 캐시 가능
                ;//.mustRevalidate();  // 만료된 경우 재검증 필요

        model.addAttribute("result", result);
        response.setHeader("Cache-Control", cacheControl.getHeaderValue());

        return htmlBasePath + "simpleModelView";
    }

    @RequestMapping("/modelMapperTest")
    public String mapperMultiObject(Model model) {
        ExampleProductDto exampleProductDto = ExampleProductDto.builder()
                .manufacturerName("samsung")
                .productName("TV")
                .productPrice(1000000L)
                .curDiscountRate(20)
                .quantity(10)
                .isAvailableReturn(true)
                .build();

        //example 1
        ExampleGoodsDto exampleGoodsDto = ModelMapperUtil.map(exampleProductDto, ExampleGoodsDto.class);

        //example 2
        ExampleGoodsNProductDto exampleGoodsNProductDto = ModelMapperUtil.map(exampleProductDto, ExampleGoodsNProductDto.class);
        ExampleADto exampleADto = ExampleADto.builder()
                .aDtoLastName("이")
                .aDtoFirstName("성일")
                .build();

        //example 3
        ExampleBDto exampleBDto = ModelMapperUtil.map(exampleADto, ExampleBDto.class);
        Map<String, Object> result = new HashMap();
        result.put("ExampleProductDto-origin", exampleProductDto);
        result.put("ExampleProductDto-exampleGoodsDto", exampleGoodsDto);
        result.put("ExampleProductDto-exampleGoodsNProductDto", exampleGoodsNProductDto);

        result.put("ExampleADto-origin", exampleADto);
        result.put("ExampleADto-exampleBDto", exampleBDto);
        model.addAttribute("result", result);

        return htmlBasePath + "simpleModelView";
    }
}

/*
-->
예전에 만들었던 객체들에서 @Builder 가 붙은 것들에 대해 잘 만들어 졌는지 확인이 필요함
ModelMapper 마무리 필요 (of 가 새 클레스 리턴전 후처리 작업을 할수 있는 람다 메소드를 지원하도록 수정 필요)

*/

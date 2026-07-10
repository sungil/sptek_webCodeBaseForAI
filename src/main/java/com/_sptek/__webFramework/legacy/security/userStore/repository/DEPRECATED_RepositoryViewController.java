package com._sptek.__webFramework.legacy.security.userStore.repository;

import com._sptek.__webFramework.view.error.Enable_ResponseOfViewGlobalException_At_ViewController;
import com._sptek.__webFramework.security.authorization.AuthorityEnum;
import com._sptek.__webFramework.security.userStore.dto.AuthorityDto;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * JPA repository 예제를 View로 확인하던 deprecated controller.
 *
 * <p>프레임워크 repository 동작 확인용 샘플이며 신규 화면 기능에서는 사용하지 않는다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@Controller
@Enable_ResponseOfViewGlobalException_At_ViewController
@RequestMapping(value = "", produces = MediaType.TEXT_HTML_VALUE)
public class DEPRECATED_RepositoryViewController {
    @NonFinal //생성자 주입 대상에서 제외
    private final String htmlBasePath = "pages/_example/html/";
    private final DEPRECATED_RepositoryService DEPRECATEDRepositoryService;
    private final ModelMapper modelMapper;

    //for test
    /**
     * key 값으로 repository 반환 타입별 예제 결과를 조회해 단순 View에 표시한다.
     */
    @GetMapping("/test/testRepo1/{key}")
    public String repoTest(@PathVariable("key") String key, Model model) {
        Map<String, Object> resultMap = DEPRECATEDRepositoryService.testRepository(key);
        model.addAttribute("result", resultMap);
        return htmlBasePath + "simpleModelView";
    }

    //for test
    /**
     * AuthorityEnum을 AuthorityDto로 매핑하는 예제 결과를 단순 View에 표시한다.
     */
    @GetMapping("/test/testRepo2")
    public String testRepo2(Model model) {
        AuthorityEnum authority = AuthorityEnum.AUTH_RETRIEVE_USER_ALL_FOR_DELIVERY;
        AuthorityDto authDto = modelMapper.map(authority, AuthorityDto.class);
        model.addAttribute("result", authDto);
        return htmlBasePath + "simpleModelView";
    }

}

package com._sptek._webFrameworkExample.unit.multipartFilePost;

import com._sptek.__webFramework.view.error.Enable_ResponseOfViewGlobalException_At_ViewController;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@Enable_ResponseOfViewGlobalException_At_ViewController
@RequestMapping(value = "/view/example/", produces = MediaType.TEXT_HTML_VALUE)

public class MultipartFilePostViewController {
    @NonFinal
    private final String htmlBasePath = "pages/_example/unit/";

    @GetMapping("/post/postWithFile")
    public String preventDuplicateRequest() {
        return htmlBasePath + "postWithFile";
    }
}

package com.sptek.__webFramework.example.unit.encryption;

import com.sptek.__webFramework.view.error.Enable_ResponseOfViewGlobalException_At_ViewController;
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

public class EncryptionViewController {
    @NonFinal
    private final String htmlBasePath = "pages/_example/unit/";

    @GetMapping("/encryption/encryptionForWeb")
    public String encryptionForWeb() {
        return htmlBasePath + "encryption";
    }
}

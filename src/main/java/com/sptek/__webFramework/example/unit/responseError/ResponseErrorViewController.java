package com.sptek.__webFramework.example.unit.responseError;

import com.sptek.__webFramework.view.error.Enable_ResponseOfViewGlobalException_At_ViewController;
import com.sptek.__webFramework.core.exception.ServiceException;
import com.sptek._projectCommon.commonObject.code.ServiceErrorCodeEnum;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/view/example/", produces = MediaType.TEXT_HTML_VALUE)
@Enable_ResponseOfViewGlobalException_At_ViewController

public class ResponseErrorViewController {
    @NonFinal
    private final String htmlBasePath = "pages/_example/unit/";

    @GetMapping({"/responseError"})
    public String responseError() {
        return htmlBasePath + "responseError";
    }

    //500
    @GetMapping({"/responseError/runtimeException"})
    public String runtimeException() {
        String nullString = null;
        if (nullString.equals("test")) {
            log.debug("temp is test");
        }
        return htmlBasePath + "index";
    }

    //400
    @GetMapping({"/responseError/serviceException"})
    public String serviceException() {
        if (true) {
            throw new ServiceException(ServiceErrorCodeEnum.DEFAULT_ERROR, "테스를 위해 임의로 발생 시킨 ServiceException");
        }
        return htmlBasePath + "index";
    }

    //403
    @GetMapping({"/responseError/authException"})
    @PreAuthorize("hasRole('ADMIN')")
    public String authException() {
        return htmlBasePath + "index";
    }
}

package com._sptek._webFrameworkExample.unit.localeLanguage;

import com._sptek.__webFramework.view.error.Enable_ResponseOfViewGlobalException_At_ViewController;
import com._sptek.__webFramework.security.support.CurrentAuthenticationUtil;
import com._sptek.__webFramework.web.locale.LocaleUtil;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RequiredArgsConstructor
@Controller
@Enable_ResponseOfViewGlobalException_At_ViewController
@RequestMapping(value = "/view/example/", produces = MediaType.TEXT_HTML_VALUE)

public class LocaleLanguageViewController {

    @NonFinal
    private final String htmlBasePath = "pages/_example/unit/";

    @RequestMapping("/localeLanguage/myLocaleLanguage")
    public String i18n(Model model) throws Exception {
        ZonedDateTime zonedDateTimeForSystem = ZonedDateTime.now(ZoneId.systemDefault());
        ZonedDateTime zonedDateTimeForUser = ZonedDateTime.now(LocaleUtil.getCurUserTimeZone().toZoneId());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String systemFormattedDateTime = zonedDateTimeForSystem.format(dateTimeFormatter);
        String userFormattedDateTime = zonedDateTimeForUser.format(dateTimeFormatter);

        String userLanguageTag = LocaleUtil.getCurUserLanguageTag();
        String userTimeZone = LocaleUtil.getCurUserTimeZoneName();

        String language = LocaleUtil.getI18nMessage("language");
        //Controller 에서 다국어 변환을 직접 하는 케이스
        String welcome = LocaleUtil.getI18nMessage("welcome"
                , new Object[] {CurrentAuthenticationUtil.getMyName()
                        , CurrentAuthenticationUtil.getMyRoles().toString()});

        model.addAttribute("systemFormattedDateTime", systemFormattedDateTime);
        model.addAttribute("userFormattedDateTime", userFormattedDateTime);

        model.addAttribute("userLanguageTag", userLanguageTag);
        model.addAttribute("userTimeZone", userTimeZone);

        model.addAttribute("language", language);
        model.addAttribute("welcome", welcome);

        return htmlBasePath + "localeLanguage";
    }
}

package com._sptek.__webFramework.view.model;

import com._sptek.__webFramework.bootstrap.annotationCondition.HasAnnotationOnMain_At_Bean;
import com._sptek.__webFramework.security.util.AuthenticationUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@Slf4j
@Data
@HasAnnotationOnMain_At_Bean(Enable_UserAuthenticationToModelAttribute_At_Main.class)
@ControllerAdvice
//@ControllerAdvice(basePackages = {"com.sptek.a", "com.aptek.b"})
//@ControllerAdvice(assignableTypes = {a.class, b.class})
@RequiredArgsConstructor
public class ControllerAdviceForUserAuthenticationToModelAttribute {

    @ModelAttribute
    //Controller, RestController 모두 에서 동작함(RestController에서는 제외하는게 맞을수 있으나.. Controller  클레스에서 Rest 기능을 구현하는 경우도 있음으로 이렇게 처리함)
    public void addModelAttributes(Model model) throws Exception {
        model.addAttribute("isLogin", AuthenticationUtil.isRealLogin());

        if (AuthenticationUtil.isRealLogin()) {
            model.addAttribute("userId", AuthenticationUtil.getMyId());
            model.addAttribute("userName", AuthenticationUtil.getMyName());
            model.addAttribute("userEmail", AuthenticationUtil.getMyEmail());
            model.addAttribute("userRoles", AuthenticationUtil.getMyRoles());
            model.addAttribute("userAuths", AuthenticationUtil.getMyAuths());
        }
    }
}

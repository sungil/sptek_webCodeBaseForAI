package com.sptek.__webFramework.view.model;

import com.sptek.__webFramework.bootstrap.annotationCondition.HasAnnotationOnMain_At_Bean;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@Slf4j
@Data
@HasAnnotationOnMain_At_Bean(Enable_PropertiesToModelAttribute_At_Main.class)
@ControllerAdvice
//@ControllerAdvice(basePackages = {"com.sptek.a", "com.aptek.b"})
//@ControllerAdvice(assignableTypes = {a.class, b.class})
@RequiredArgsConstructor
public class ControllerAdviceForPropertiesToModelAttribute {
    private final PropertiesForModelAttributeVo propertiesForModelAttributeVo;

    @ModelAttribute
    //Controller, RestController 모두 에서 동작함(RestController에서는 제외하는게 맞을수 있으나.. Controller  클레스에서 Rest 기능을 구현하는 경우도 있음으로 이렇게 처리함)
    public void addModelAttributes(Model model) {
        propertiesForModelAttributeVo.getAttributes()
                .forEach(model::addAttribute);
    }

//    @PostConstruct //Bean 생성 이후 호출
//    public void init() {
//        log.debug("StaticModelAttribute from property : {}", this);
//    }
}

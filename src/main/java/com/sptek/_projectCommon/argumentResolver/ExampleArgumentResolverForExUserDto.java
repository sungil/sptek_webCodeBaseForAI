package com.sptek._projectCommon.argumentResolver;

import com.sptek.__webFramework.web.binding.Enable_ArgumentResolver_At_Param;
import com.sptek.__webFramework.example.dto.ExUserDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.IOException;

@Component
public class ExampleArgumentResolverForExUserDto implements HandlerMethodArgumentResolver {
    //Controller 에만 적용 가능함

    @Override //적용 조건 설정
    public boolean supportsParameter(MethodParameter methodParameter) {
        //해당 클레스와 정확히 일치하는 경우만 적용할때
        //return methodParameter.getParameterType().equals(MyUser.class);

        //해당 클레스를 상속받은 클레스까지 적용할때
        //return methodParameter.getParameterType().isAssignableFrom(MyUserDto.class)

        //해당 클레스를 상속받은 클레스까지 적용하지만 rgumentResolver를 적용하겠다는 별도의 어노테이션이 있는 경우만 처리
        return methodParameter.getParameterType().isAssignableFrom(ExUserDto.class)
                && methodParameter.hasParameterAnnotation(Enable_ArgumentResolver_At_Param.class);
    }


    @Override
    //MethodParameter, modelAndViewContainer, nativeWebRequest, webDataBinderFactory 을 응용 해서 더 많은 곳에 활용 가능.
    public Object resolveArgument(
            @NotNull MethodParameter methodParameter
            , ModelAndViewContainer modelAndViewContainer
            , NativeWebRequest nativeWebRequest
            , WebDataBinderFactory webDataBinderFactory) throws IOException {

        String id = nativeWebRequest.getParameter("id");
        String name = nativeWebRequest.getParameter("name");
        String type = nativeWebRequest.getParameter("type");

        return ExUserDto.builder()
                .id(id)
                .name(name)
                .type(ExUserDto.UserType.valueOf(type))
                .displayName(String.format("%s 님 (%s/%s) 안녕 하세요!", name, id, type))
                .build();
    }
}

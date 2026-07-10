package com._sptek._webFrameworkExample.aiExample.web.argument;

import com._sptek.__webFramework.web.binding.Enable_ArgumentResolver_At_Param;
import com._sptek._webFrameworkExample.aiExample.common.dto.AiExampleUserDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class AiExampleUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(AiExampleUserDto.class)
                && parameter.hasParameterAnnotation(Enable_ArgumentResolver_At_Param.class);
    }

    @Override
    public Object resolveArgument(
            @NotNull MethodParameter parameter,
            ModelAndViewContainer modelAndViewContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        String id = valueOrDefault(webRequest.getParameter("id"), "anonymous");
        String name = valueOrDefault(webRequest.getParameter("name"), "Anonymous");
        String typeValue = valueOrDefault(webRequest.getParameter("type"), AiExampleUserDto.UserType.ANONYMOUS.name());
        AiExampleUserDto.UserType type = AiExampleUserDto.UserType.valueOf(typeValue.toUpperCase());

        return AiExampleUserDto.builder()
                .id(id)
                .name(name)
                .type(type)
                .displayName("%s (%s/%s)".formatted(name, id, type))
                .build();
    }

    private String valueOrDefault(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }
}

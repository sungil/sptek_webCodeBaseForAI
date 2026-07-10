package com._sptek.__webFramework.web.xss;

import com._sptek.__webFramework.core.constant.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
/**
 * 응답 직렬화 직전에 문자열 값을 HTML escape 처리하는 지원 컴포넌트.
 *
 * <p>문자열, 컬렉션, 배열, Map, 프로젝트 DTO 객체를 재귀적으로 순회한다. DTO 객체는
 * 새 객체를 만들지 않고 필드 값을 직접 변경하므로 호출 지점에서는 원본 객체 변경을 전제로 사용해야 한다.</p>
 */
public class XssEscapeSupport {

    /**
     * 전달된 값을 타입별로 판별해 XSS 방어용 HTML escape 처리를 수행한다.
     */
    public @Nullable Object escape(Object value) {
        if (value == null) return null;

        if (value instanceof String str) {
            return StringEscapeUtils.escapeHtml4(str);
        }

        if (value instanceof List<?> list) {
            return list.stream().map(this::escape).collect(Collectors.toList());
        }

        if (value instanceof Set<?> set) {
            return set.stream().map(this::escape).collect(Collectors.toSet());
        }

        if (value instanceof Map<?, ?> map) {
            return map.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> escape(e.getValue()),
                            (a, b) -> b,
                            LinkedHashMap::new
                    ));
        }

        if (value instanceof Object[] array) {
            return Arrays.stream(array).map(this::escape).toArray();
        }

        if (isMyDtoObject(value)) {
            return escapeDtoObject(value);
        }

        return value;
    }

    /**
     * 프레임워크 프로젝트 패키지에 속한 DTO/값 객체만 재귀 escape 대상으로 취급한다.
     */
    private boolean isMyDtoObject(Object obj) {
        return obj != null
                && !(obj instanceof Enum)
                && obj.getClass().getPackageName().startsWith(CommonConstants.PROJECT_PACKAGE_NAME);
    }

    /**
     * DTO 객체의 선언 필드와 상위 클래스 필드를 순회하며 문자열 필드를 escape 한다.
     */
    private Object escapeDtoObject(Object dto) {
        for (Field field : getAllFields(dto.getClass())) {
            field.setAccessible(true);
            try {
                Object fieldValue = field.get(dto);
                if (fieldValue instanceof String str) {
                    field.set(dto, StringEscapeUtils.escapeHtml4(str));
                } else if (fieldValue instanceof List<?> || fieldValue instanceof Map<?, ?> || isMyDtoObject(fieldValue)) {
                    field.set(dto, escape(fieldValue));
                }
            } catch (Exception e) {
                log.warn("Xss Protecting Fail - class: {}, field: {}", dto.getClass().getName(), field.getName(), e);
            }
        }
        return dto;
    }

    /**
     * 상속 구조에 선언된 모든 필드를 하위 클래스부터 순서대로 수집한다.
     */
    private List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        while (type != null && type != Object.class) {
            fields.addAll(Arrays.asList(type.getDeclaredFields()));
            type = type.getSuperclass();
        }
        return fields;
    }
}

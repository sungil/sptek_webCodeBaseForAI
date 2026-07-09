package com.sptek.__webFramework.core.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * JSON, Map, Collection, HTTP header 등 데이터 표현 간 변환을 제공하는 유틸리티.
 */
@Slf4j
public class TypeConvertUtil {
    private static ObjectMapper objectMapperWithRootName = null; //

    /**
     * 문자열 Map을 로그에 적합한 {@code key: value} 나열 문자열로 변환한다.
     */
    public static String strMapToString(Map<String, String> originMap){
        return originMap.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));
    }

    /**
     * request parameter Map처럼 값이 문자열 배열인 Map을 로그 문자열로 변환한다.
     */
    public static String strArrMapToString(Map<String, String[]> originMap){
        return originMap.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + Arrays.toString(entry.getValue()))
                .collect(Collectors.joining(", "));
    }

    /**
     * MultiValueMap 형태의 문자열 List 값을 로그 문자열로 변환한다.
     */
    public static String strListMapToString(Map<String, List<String>> originMap) {
        return originMap.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));
    }

    /**
     * Spring Bean으로 등록된 ObjectMapper를 사용해 root name 없이 JSON 문자열을 만든다.
     */
    public static String objectToJsonWithoutRootName(@Nullable Object object, boolean prettyPrintOption) throws JsonGenerationException, JsonMappingException, IOException {
        if (object == null) {
            return "{}";

        } else {
            if (prettyPrintOption) {
                return SpringUtil.getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object);
            } else {
                return SpringUtil.getObjectMapper().writeValueAsString(object);
            }
        }
    }

    /**
     * 별도 ObjectMapper를 사용해 root name을 감싼 JSON 문자열을 만든다.
     */
    public static String objectToJsonWithRootName(Object object, boolean prettyPrintOption) throws JsonGenerationException, JsonMappingException, IOException {
        // config 변경이 필요함으로(쓰레드 세이프하지 않음) 빈이 아닌 별도로 생성하여 사용
        if (objectMapperWithRootName == null) {
            objectMapperWithRootName = new ObjectMapper();
            objectMapperWithRootName.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapperWithRootName.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
            objectMapperWithRootName.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
        }

        if (prettyPrintOption) {
            return objectMapperWithRootName.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } else {
            return objectMapperWithRootName.writeValueAsString(object);
        }
    }

    /**
     * 객체를 JSON으로 직렬화해 전달된 OutputStream에 바로 기록한다.
     */
    public static void objectToJasonWithOutputStream(OutputStream outputStream, Object object, boolean prettyPrintOption) throws JsonGenerationException, JsonMappingException, IOException {
        if (prettyPrintOption) {
            SpringUtil.getObjectMapper().writerWithDefaultPrettyPrinter().writeValue(outputStream, object);
        } else {
            SpringUtil.getObjectMapper().writeValue(outputStream, object);
        }
    }

    /**
     * JSON 문자열을 {@code Map<String, Object>}로 역직렬화한다.
     */
    public static Map<String, Object> jsonToMap(String json) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<HashMap<String, Object>>() {});
    }

    /**
     * JSON 문자열을 지정 클래스로 역직렬화한다.
     */
    public static <T> T jsonToClass(String json, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, clazz);
    }

    /**
     * 제네릭 타입 정보를 포함한 TypeReference 기준으로 JSON 문자열을 역직렬화한다.
     */
    public static <T> T jsonToClass(String json, TypeReference<T> type) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, type);
    }

    /**
     * keyExtractor 결과가 myThings에 존재하는 origin 항목만 찾아 반환한다.
     */
    public static <T> List<T> findMatchingOrigins(List<T> origins, List<T> myThings, Function<T, ?> keyExtractor) {
        List<?> myThingKyes = Optional.ofNullable(myThings).orElseGet(Collections::emptyList).stream()
                .map(keyExtractor)
                .toList();

        return origins.stream()
                .filter(origin -> myThingKyes.contains(keyExtractor.apply(origin)))
                .collect(Collectors.toList());
    }

    /**
     * Enumeration 값을 Set으로 복사한다.
     */
    public static Set<String> enumerationToSet(Enumeration<String> enumeration) {
        Set<String> set = new HashSet<>();
        while (enumeration.hasMoreElements()) {
            set.add(enumeration.nextElement());
        }
        return set;
    }

    /**
     * Collection 값을 Set으로 복사한다.
     */
    public static Set<String> collectionToSet(Collection<String> collection) {
        Set<String> set = new HashSet<>(collection);
        return set;
    }

    /**
     * Collection 값을 Enumeration으로 감싼다.
     */
    public static Enumeration<String> collectionToEnumeration(Collection<String> collection) {
        return Collections.enumeration(collection);
    }

    /**
     * Enumeration 값을 List로 복사한다.
     */
    public static List<String> enumerationToList(Enumeration<String> enumeration) {
        List<String> list = new ArrayList<>();
        while (enumeration.hasMoreElements()) {
            list.add(enumeration.nextElement());
        }
        return list;
    }

    /**
     * 문자열, 문자열 List, 문자열 Set 값을 가진 Map을 Spring HttpHeaders로 변환한다.
     */
    public static HttpHeaders objMapToHttpHeaders(Map<String, Object> headerMap) {
        HttpHeaders httpHeaders = new HttpHeaders();

        for (Map.Entry<String, Object> entry : headerMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof String) {
                httpHeaders.add(key, (String) value);
            } else if (value instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> list = (List<String>) value;
                httpHeaders.addAll(key, list);
            } else if (value instanceof Set) {
                @SuppressWarnings("unchecked")
                Set<String> set = (Set<String>) value;
                httpHeaders.addAll(key, new ArrayList<>(set));
            } else {
                throw new IllegalArgumentException("Unsupported header value type for key: " + key);
            }
        }

        return httpHeaders;
    }
}


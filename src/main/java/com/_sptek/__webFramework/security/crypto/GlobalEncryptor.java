package com._sptek.__webFramework.security.crypto;

import jakarta.validation.constraints.NotNull;
import org.jasypt.encryption.StringEncryptor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 프레임워크 전역에서 여러 문자열 암복호화 모듈을 같은 형식으로 호출하게 하는 진입점.
 *
 * <p>각 암호화 모듈은 Spring Bean 초기화 시 자신을 등록하고, 이 클래스는
 * {@code ENC_sptTYPE(...)} 형식의 wrapper 문자열을 기준으로 복호화 모듈을 선택한다.
 * DTO 복호화는 명시적으로 표시된 String 필드만 대상으로 삼아 일반 필드 변환과 구분한다.</p>
 */
public class GlobalEncryptor {
    /**
     * 전역 암복호화 wrapper 에 기록되는 모듈 식별자.
     */
    public enum Type {
        sptAES, sptDES, sptJASYPT, sptRSA;
    }

    private static final String encKeyword = "ENC_";
    private static final Map<String, StringEncryptor> encryptorMap = new ConcurrentHashMap<>();

    /**
     * 전역 암복호화 호출에 사용할 모듈 구현체를 등록한다.
     *
     * <p>각 encryptModule 구현체가 생성 시점에 호출하며, 같은 타입을 다시 등록하면
     * 이후 호출에서는 마지막으로 등록된 구현체가 사용된다.</p>
     */
    public static void register(Type encryptorTypeEnum, StringEncryptor stringEncryptor) {
        encryptorMap.put(encryptorTypeEnum.name(), stringEncryptor);
    }

    /**
     * 지정한 암호화 타입으로 평문을 암호화하고 전역 복호화가 해석할 수 있는 wrapper 문자열로 반환한다.
     *
     * <p>반환값은 {@code ENC_sptTYPE(...)} 형식이며, 등록되지 않은 타입이면 예외를 던진다.</p>
     */
    public static String encrypt(Type encryptorTypeEnum, @NotNull String plainText) {
        StringEncryptor stringEncryptor = encryptorMap.get(encryptorTypeEnum.name());
        if (stringEncryptor == null) {
            throw new IllegalArgumentException(String.format("%s 는 지원하지 않는 암호화 타입 입니다.", encryptorTypeEnum.name()));
        }
        return String.format("%s%s(%s)", encKeyword, encryptorTypeEnum.name(), stringEncryptor.encrypt(plainText));
    }

//    //dto 의 모든 필드를 검사해서 decrypt 하는 방식 (리소스 다소 낭비)
//    public static <T> T decrypt(@NotNull T dto) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
//        Class<?> clazz = dto.getClass();
//        Object copy = clazz.getDeclaredConstructor().newInstance();
//
//        for (Field field : clazz.getDeclaredFields()) {
//            field.setAccessible(true);
//            Object originalField = field.get(dto);
//
//            if (originalField instanceof String originalFieldValue && originalFieldValue.startsWith(encKeyword)) {
//                String decryptedOriginalFieldValue = decrypt(originalFieldValue);
//                field.set(copy, decryptedOriginalFieldValue);
//            } else if (originalField != null && !field.getType().isPrimitive() && !field.getType().getName().startsWith("java.")) {
//                // 재귀: 내부 DTO도 깊은 복사 + 복호화
//                Object decryptedChildField = decrypt(originalField);
//                field.set(copy, decryptedChildField);
//            } else {
//                // 단순 복사 (String or 기타 primitive-like)
//                field.set(copy, originalField);
//            }
//        }
//
//        @SuppressWarnings("unchecked")
//        T castedCopy = (T) copy;
//        return castedCopy;
//    }

    /**
     * DTO 객체를 복사하면서 자동 복호화 대상으로 표시된 String 필드를 복호화한다.
     *
     * <p>원본 객체를 직접 수정하지 않고 기본 생성자로 새 객체를 만든다. 순환 참조나 공유 객체가 있을 수 있어
     * 이미 확인한 객체는 다시 순회하지 않는다.</p>
     */
    public static <T> T decrypt(@NotNull T dto) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        return decryptInternal(dto, visited);
    }

    /**
     * DTO 필드 중 {@link Enable_DecryptAuto_At_DtoString}이 붙은 암호화 문자열만 복호화한다.
     *
     * <p>일반 String 필드는 그대로 복사하고, 사용자 정의 객체 필드는 재귀적으로 복사한다.
     * 복잡한 DTO 그래프에서는 reflection 순회 비용을 고려해야 한다.</p>
     */
    private static <T> T decryptInternal(@NotNull T dto, Set<Object> visited)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {

        // 중복 객체 방지
        if (visited.contains(dto)) return dto;
        visited.add(dto);

        Class<?> clazz = dto.getClass();
        Object copy = clazz.getDeclaredConstructor().newInstance();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            Object originalField = field.get(dto);

            if (field.isAnnotationPresent(Enable_DecryptAuto_At_DtoString.class)
                    && originalField instanceof String strValue
                    && strValue.startsWith(encKeyword)) {

                String decrypted = decrypt(strValue);
                field.set(copy, decrypted);
            }

            // 재귀 복호화
            else if (originalField != null
                    && !field.getType().isPrimitive()
                    && !field.getType().getName().startsWith("java.")) {

                Object decryptedChild = decryptInternal(originalField, visited);
                field.set(copy, decryptedChild);
            }

            // 나머지 단순 복사
            else {
                field.set(copy, originalField);
            }
        }

        @SuppressWarnings("unchecked")
        T castedCopy = (T) copy;
        return castedCopy;
    }

    /**
     * 전역 wrapper 문자열에서 암호화 타입과 실제 암호문을 분리해 복호화한다.
     *
     * <p>{@code ENC_sptTYPE(...)} 형식이 아니거나 해당 타입의 구현체가 등록되지 않은 경우 예외를 던진다.</p>
     */
    public static String decrypt(@NotNull String encryptedText) {
        for (Type encryptorTypeEnum : Type.values()) {
            String prefix = encKeyword + encryptorTypeEnum.name() + "(";

            if (encryptedText.startsWith(prefix) && encryptedText.endsWith(")")) {
                String extractedEncryptedText = encryptedText.substring(prefix.length(), encryptedText.length() - 1);
                StringEncryptor stringEncryptor = encryptorMap.get(encryptorTypeEnum.name());
                if (stringEncryptor == null) {
                    throw new IllegalArgumentException(String.format("%s 는 지원하지 않는 암호화 타입 입니다.", encryptorTypeEnum.name()));
                }
                return stringEncryptor.decrypt(extractedEncryptedText);
            }
        }
        throw new IllegalArgumentException("지원하지 않는 암호화 타입 입니다.");
    }

}

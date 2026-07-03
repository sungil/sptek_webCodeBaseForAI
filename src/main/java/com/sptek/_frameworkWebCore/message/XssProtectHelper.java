package com.sptek._frameworkWebCore.message;

import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SerializedString;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
Xss 방지 적용을 위한 클레스로 버그가 있지않는 한 수정할 부분은 없다.
 */
/**
 * Jackson JSON 직렬화 단계에서 XSS 위험 문자를 HTML escape 문자열로 치환하는 CharacterEscapes 구현.
 *
 * <p>{@code <, >, ", ', &} 문자를 custom escape 대상으로 지정하고,
 * 같은 문자에 대한 escape 결과는 cache해 반복 직렬화 비용을 줄인다.</p>
 */
public class XssProtectHelper extends CharacterEscapes {
    private final int[] asciiEscapes;
    private final Map<Integer, SerializedString> escapeCache = new ConcurrentHashMap<>();

    /**
     * XSS 방지 대상 ASCII 문자에 custom escape 코드를 설정한다.
     */
    public XssProtectHelper() {
        asciiEscapes = CharacterEscapes.standardAsciiEscapesForJSON();
        asciiEscapes['<'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['>'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['\"'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['\''] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['&'] = CharacterEscapes.ESCAPE_CUSTOM;

        /*
        // 추가 적용 고려 가능
        asciiEscapes['('] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes[')'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['#'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['/'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['='] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['+'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes[';'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['%'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['\\'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes[':'] = CharacterEscapes.ESCAPE_CUSTOM;
        */
    }

    /**
     * Jackson이 ASCII 문자별 escape 정책을 조회할 때 사용하는 배열을 반환한다.
     */
    @Override
    public int[] getEscapeCodesForAscii() {
        return asciiEscapes;
    }

//    @Override
//    public SerializableString getEscapeSequence(int ch) {
//        return new SerializedString(StringEscapeUtils.escapeHtml4(Character.toString((char) ch)));
//    }

    /**
     * custom escape 대상 문자를 HTML4 escape 문자열로 변환한다.
     */
    @Override
    public SerializableString getEscapeSequence(int ch) {
        return escapeCache.computeIfAbsent(ch, key ->
                new SerializedString(
                        StringEscapeUtils.escapeHtml4(Character.toString((char) key.intValue()))
                )
        );
    }
}



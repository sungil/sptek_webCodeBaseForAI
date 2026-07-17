package com._sptek.__webFramework.web.xss;

import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SerializedString;

/**
 * JSON 문자열 값의 데이터 의미는 유지하면서 HTML/script 문맥에서 위험한 문자를 Unicode escape로 기록한다.
 */
public class JsonUnicodeEscapeCharacterEscapes extends CharacterEscapes {
    private static final SerializedString ESCAPE_LT = new SerializedString("\\u003C");
    private static final SerializedString ESCAPE_GT = new SerializedString("\\u003E");
    private static final SerializedString ESCAPE_AMP = new SerializedString("\\u0026");
    private static final SerializedString ESCAPE_APOS = new SerializedString("\\u0027");

    private final int[] asciiEscapes;

    public JsonUnicodeEscapeCharacterEscapes() {
        asciiEscapes = CharacterEscapes.standardAsciiEscapesForJSON();
        asciiEscapes['<'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['>'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['&'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['\''] = CharacterEscapes.ESCAPE_CUSTOM;
    }

    @Override
    public int[] getEscapeCodesForAscii() {
        return asciiEscapes;
    }

    @Override
    public SerializableString getEscapeSequence(int ch) {
        return switch (ch) {
            case '<' -> ESCAPE_LT;
            case '>' -> ESCAPE_GT;
            case '&' -> ESCAPE_AMP;
            case '\'' -> ESCAPE_APOS;
            default -> null;
        };
    }
}
